#!/usr/bin/env python
# -*- coding: utf-8 -*-
import sys
import os
import glob
import shutil


def format_line(line, settings):
    for key in settings:
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
    settings['ArgoTemplate'] = settings['_Project_']
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

if __name__ == '__main__':
    main()
