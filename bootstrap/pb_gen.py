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


def gen_protobuf_def(module, folder, settings):
    base_folder = os.path.join(settings['_root_'], '_Project_-Protobuf/src/main/')
    base_folder = format_line(base_folder, settings)
    if not os.path.exists(base_folder):
        os.makedirs(base_folder)
    #mapper
    outname = string.capitalize(module['name']) + 'Proto.proto'
    proto_file = os.path.join(base_folder, outname)
    kwargs = {}
    kwargs.update(settings)
    kwargs['now'] = datetime.now()
    kwargs['_module_'] = module['name']
    kwargs['_moduleC_'] = string.capitalize(module['name'])
    kwargs['_ms_'] = module['ref']
    print module['name'], kwargs['_ms_']
    #entity and service
    _tbis = []
    for tbl in module['tables']:
        tbi = dbm.get_table(module, tbl)
        _tbis.append(tbi)
    kwargs['_tbis'] = _tbis
    javagen.render_protobuf(proto_file, **kwargs)


def gen_protobuf_impl(module, folder, settings):
    base_folder = os.path.join(settings['_root_'], '_Project_-Protobuf/src/main/')
    base_folder = format_line(base_folder, settings)
    base_folder = base_folder.replace('\\', '/')
    #mapper
    folder0 = os.path.join(folder, 'protobuf', module['name'])
    print folder0
    os.makedirs(folder0)
    outname = string.capitalize(module['name']) + 'Proto.proto'
    proto_file = os.path.join(base_folder, outname)
    kwargs = {}
    kwargs.update(settings)
    kwargs['now'] = datetime.now()
    kwargs['_module_'] = module['name']
    kwargs['_moduleC_'] = string.capitalize(module['name'])
    #entity and service
    _tbis = []
    for tbl in module['tables']:
        tbi = dbm.get_table(module, tbl)
        _tbis.append(tbi)
    kwargs['_tbis'] = _tbis
    #java out
    java_out = os.path.join(settings['_root_'], '_Project_-Protobuf/src/main/java')
    java_out = format_line(java_out, settings)
    cmd = 'protoc --proto_path=%s --java_out=%s %s' % (base_folder, java_out, proto_file)
    print cmd
    os.system(cmd)
    #render java wrapper
    for tbl in _tbis:
        kwargs['_tbi'] = tbl
        fnamet = os.path.join(folder0, 'P%sWrapper.java' % tbl.entityName)
        javagen.render_protobuf_wrapper(fnamet, **kwargs)
    #ios out
    cpp_out = os.path.join(settings['_root_'], '_Project_-Protobuf/src/main/ios/Models', module['name'])
    cpp_out = format_line(cpp_out, settings)
    os.makedirs(cpp_out)
    cmd = 'protoc --proto_path=%s --cpp_out=%s %s' % (base_folder, cpp_out, proto_file)
    print cmd
    os.system(cmd)
    #rename cpp to .hh and .mm for iOS
    fname = os.path.join(cpp_out, outname.replace('.proto', ''))
    print fname
    os.rename(fname + '.pb.h', fname + '.pb.hh')
    os.rename(fname + '.pb.cc', fname + '.pb.mm')
    with open(fname + '.pb.mm', 'r') as f:
        txt = f.read()
        txt = txt.replace('Proto.pb.h', 'Proto.pb.hh')
        with open(fname + '.pb.mm', 'w+') as fw:
            fw.write(txt)
    with open(fname + '.pb.hh', 'r') as f:
        txt = f.read()
        txt = txt.replace('Proto.pb.h', 'Proto.pb.hh')
        with open(fname + '.pb.hh', 'w+') as fw:
            fw.write(txt)
    #generate ios
    for tbl in _tbis:
        kwargs['_tbi'] = tbl
        fname = os.path.join(cpp_out, 'TS' + tbl.entityName)
        javagen.render_ios(fname, **kwargs)


def start(settings):
    ms = settings['_modules_']
    #folder info
    protobuf_folder = os.path.join(settings['_root_'], '_Project_-Protobuf/src/main/java/com/company/_project_')
    protobuf_folder = format_line(protobuf_folder, settings)
    if os.path.exists(protobuf_folder):
        shutil.rmtree(protobuf_folder)
    #tables
    tbm = {}
    for m in ms:
        mf = ms[m]
        mf['name'] = m
        for tbl in mf['tables']:
            tbm[tbl] = m
    #print tbm
    settings['_tbm_'] = tbm
    for m in settings['_order_']:
        mf = ms[m]
        mf['name'] = m
        dbm.open(mf, settings)
        gen_protobuf_def(mf, protobuf_folder, settings)
        gen_protobuf_impl(mf, protobuf_folder, settings)


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
