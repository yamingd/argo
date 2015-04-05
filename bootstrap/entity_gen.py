#!/usr/bin/env python
# -*- coding: utf-8 -*-
import sys
import os
import shutil
import dbm
import javagen
import string
from datetime import datetime


def format_line(line, settings):
    for key in settings:
        v = settings[key]
        if isinstance(v, str) or isinstance(v, unicode):
            line = line.replace(key, settings[key])
    return line


def gen_entity_def(module, folder, settings):
    #mapper
    fname = os.path.join(folder, string.capitalize(module['name']) + 'Mappers.java')
    kwargs = {}
    kwargs.update(settings)
    kwargs['now'] = datetime.now()
    kwargs['_module_'] = module['name']
    kwargs['_moduleC_'] = string.capitalize(module['name'])
    kwargs['_entitys_'] = []
    for tbl in module['tables']:
        kwargs['_entitys_'].append(dbm.java_name(tbl))
    javagen.render_mapper(fname, **kwargs)
    kwargs.pop('_entitys_')
    #entity and service
    for tbl in module['tables']:
        tbi = dbm.get_table(module, tbl)
        kwargs['_tbi_'] = tbi
        fname = os.path.join(folder, tbi.entityName + '.java')
        kwargs['_cols_'] = tbi.columns
        kwargs['_pks_'] = tbi.pks
        #entity
        javagen.render_entity(fname, **kwargs)
        fname = os.path.join(folder, 'service', tbi.entityName + 'Tx.java')
        #db-tx
        javagen.render_tx(fname, **kwargs)


def start(settings):
    ms = settings['_modules_']
    core_folder = os.path.join(settings['_root_'], '_Project_-Core/src/main/java/com/company/_project_')
    core_folder = format_line(core_folder, settings)
    if os.path.exists(core_folder):
        shutil.rmtree(core_folder)
    #tables
    tbm = {}
    for m in ms:
        mf = ms[m]
        mf['name'] = m
        for tbl in mf['tables']:
            tbm[tbl] = m
    #print tbm
    settings['_tbm_'] = tbm
    for m in ms:
        mf = ms[m]
        mf['name'] = m
        dbm.open(mf, settings)
        # entity def
        folder = os.path.join(core_folder, m)
        os.makedirs(folder)
        # service def
        folder1 = os.path.join(folder, 'service')
        os.makedirs(folder1)
        # start
        gen_entity_def(mf, folder, settings)


def main():
    name = sys.argv[1]
    print 'Project Cfg: ', name
    temp = __import__(name, globals(), locals(), ['settings'], -1)
    # print temp
    settings = getattr(temp, 'settings')
    print settings
    root = os.path.join(settings['_output_'], settings['_Project_'])
    settings['_root_'] = root
    # project modules
    start(settings)


if __name__ == '__main__':
    main()
