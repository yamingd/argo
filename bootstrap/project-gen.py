#!/usr/bin/env python
# -*- coding: utf-8 -*-
import sys
import os
import glob
import shutil
import dbm
import javagen
import string
from datetime import datetime


def format_line(line, settings):
    for key in settings:
        if key not in ['_modules_', '_mysql_']:
            line = line.replace(key, settings[key])
    return line


def copy_file(src, dst, settings):
    with open(dst, 'w+') as fw:
        with open(src) as fr:
            for line in fr:
                fw.write(format_line(line, settings))


def gen_file(src, dst, settings):
    print src
    print dst
    if os.path.isdir(src):
        os.makedirs(dst)
    else:
        copy_file(src, dst, settings)


def gen_structs(settings):
    root = settings['_root_']
    folders = os.listdir('template')
    print folders
    for folder in folders:
        dst = format_line(folder, settings)
        gen_file(os.path.join('template', folder), os.path.join(root, dst), settings)
    return folders


def gen_service_def(module, folder, test_folder, settings):
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
        kwargs['_service_'] = tbi.entityName + 'Service'
        fname = os.path.join(folder, 'service', tbi.entityName + 'Service.java')
        #service
        javagen.render_service(fname, **kwargs)
        fname = os.path.join(folder, 'service', tbi.entityName + 'Tx.java')
        #db-tx
        javagen.render_tx(fname, **kwargs)
        # service test
        fname = os.path.join(test_folder, tbi.entityName + 'ServiceTest.java')
        javagen.render_service_test(fname, **kwargs)


def gen_service_impl(module, folder, settings):
    kwargs = {}
    kwargs.update(settings)
    kwargs['now'] = datetime.now()
    kwargs['_module_'] = module['name']
    kwargs['_moduleC_'] = string.capitalize(module['name'])
    #entity and service
    for tbl in module['tables']:
        name = dbm.java_name(tbl)
        kwargs['_entity_'] = name
        kwargs['_tblname_'] = tbl
        fname = os.path.join(folder, name + 'ServiceImpl.java')
        javagen.render_service_impl(fname, **kwargs)
        

def gen_controller_impl(module, folder, test_folder, settings):
    kwargs = {}
    kwargs.update(settings)
    kwargs['now'] = datetime.now()
    kwargs['_module_'] = module['name']
    kwargs['_moduleC_'] = string.capitalize(module['name'])
    #entity and service
    for tbl in module['tables']:
        name = dbm.java_name(tbl)
        kwargs['_entity_'] = name
        kwargs['_entityL_'] = dbm.java_name(tbl, upperFirst=False)
        url = tbl.replace(module['name'], '')
        if url.startswith('_'):
            url = url[1:]
        if url.endswith('_'):
            url = url[0:-1]
        url = '/'.join(url.split('_'))
        if url.endswith('/'):
            url = url[0:-1]
        if len(url) > 0:
            url = module['name'] + '/' + url
        else:
            url = module['name']
        kwargs['_mvcurl_'] = url
        # table info
        tbi = dbm.get_table(module, tbl)
        cols = []
        for c in tbi.columns:
            if c.isString:
                cols.append(c)
        kwargs['_cols_'] = cols
        kwargs['_tbi_'] = tbi
        # render controller
        fname = os.path.join(folder, name + 'Controller.java')
        javagen.render_controller(fname, **kwargs)
        # render controller form
        fname = os.path.join(folder, name + 'Form.java')
        javagen.render_form(fname, **kwargs)
        # gen controller test
        fname = os.path.join(test_folder, name + 'ControllerTest.java')
        javagen.render_controller_test(fname, **kwargs)
        # make view folder
        folder2 = os.path.join(settings['_root_'], 'Web/src/main/webapp/WEB-INF/views/admin')
        folder2 = os.path.join(folder2, kwargs['_mvcurl_'])
        if not os.path.exists(folder2):
            os.makedirs(folder2)
        for name in ['add', 'view', 'list']:
            f = os.path.join(folder2, name + '.ftl')
            with open(f, 'w+') as fw:
                fw.write('')
        

def gen_jdbc_yml(settings):
    ms = settings['_modules_']
    kwargs = {'_modules_': ms}
    folder = os.path.join(settings['_root_'], 'Web/src/main/resources/dev')
    fname = os.path.join(folder, 'jdbc.yaml')
    javagen.render_jdbc_yaml(fname, **kwargs)


def gen_modules(settings):
    ms = settings['_modules_']
    core_folder = os.path.join(settings['_root_'], '_Project_-Core/src/main/java/com/company/_project_')
    core_folder = format_line(core_folder, settings)
    service_folder = os.path.join(settings['_root_'], '_Project_-Service/src/main/java/com/company/_project_')
    service_folder = format_line(service_folder, settings)
    controller_folder = os.path.join(settings['_root_'], '_Project_-Controller/src/main/java/com/company/_project_/web/controllers/admin')
    controller_folder = format_line(controller_folder, settings)
    controller_test_folder = os.path.join(settings['_root_'], '_Project_-TestCase/src/main/java/com/company/_project_/testcases/controller/admin')
    controller_test_folder = format_line(controller_test_folder, settings)
    service_test_folder = os.path.join(settings['_root_'], '_Project_-TestCase/src/main/java/com/company/_project_/testcases/service')
    service_test_folder = format_line(service_test_folder, settings)

    for m in ms:
        mf = ms[m]
        mf['name'] = m
        dbm.open(mf, settings)
        # service def
        folder = os.path.join(core_folder, m)
        os.makedirs(folder)
        folder1 = os.path.join(folder, 'service')
        os.makedirs(folder1)
        folder2 = os.path.join(service_test_folder, m)
        os.makedirs(folder2)
        gen_service_def(mf, folder, folder2, settings)
        # service impl
        folder = os.path.join(service_folder, m)
        os.makedirs(folder)
        folder1 = os.path.join(folder, 'service', 'impl')
        os.makedirs(folder1)
        gen_service_impl(mf, folder1, settings)
        # controller impl
        folder = os.path.join(controller_folder, m)
        os.makedirs(folder)
        folder2 = os.path.join(controller_test_folder, m)
        os.makedirs(folder2)
        gen_controller_impl(mf, folder, folder2, settings)
    

def main():
    name = sys.argv[1]
    print 'Project Cfg: ', name
    temp = __import__(name, globals(), locals(), ['settings'], -1)
    # print temp
    settings = getattr(temp, 'settings')
    print settings
    root = os.path.join(settings['_output_'], settings['_Project_'])
    if os.path.exists(root):
        shutil.rmtree(root)
    os.makedirs(root)
    settings['_root_'] = root
    folders = gen_structs(settings)
    _tmpl_ = 'template/'
    for folder in folders:
        base = _tmpl_ + folder
        files = glob.glob(base + '/*')
        while files:
            fname = files.pop()
            # print fname
            dst = fname[len(_tmpl_):]
            dst = format_line(dst, settings)
            dst = os.path.join(root, dst)
            if os.path.isdir(fname):
                gen_file(fname, dst, settings)
                files.extend(glob.glob(fname + '/*'))
            else:
                gen_file(fname, dst, settings)
    # project modules
    gen_modules(settings)
    gen_jdbc_yml(settings)

if __name__ == '__main__':
    main()
