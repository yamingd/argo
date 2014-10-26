#!/usr/bin/env python
# -*- coding: utf-8 -*-
import sys
import dbm
import sqlite3
from cStringIO import StringIO


def gen_table(module, settings):
    name = module.pop('name')
    conn = sqlite3.connect(name + ".db3")
    c = conn.cursor()
    #entity and service
    for db in module:
        tbs = module[db]
        m = {'db': db}
        dbm.open(m, settings)
        for tb in tbs:
            tbi = dbm.get_table(m, tb)
            sql = StringIO()
            sql.write(' create table IF NOT EXISTS %s (' % tb)
            for col in tbi.columns:
                sql.write('%s %s, ' % (col.name, col.sqlite3_type))
            sql.write('_ts_ timestamp')
            sql.write(')')
            sql = sql.getvalue()
            print sql
            c.execute(sql)
    conn.commit()


def gen_db3s(settings):
    ms = settings['_sqlite3_']
    for name in ms:
        mf = ms[name]
        mf['name'] = name
        gen_table(mf, settings)


def main():
    name = sys.argv[1]
    print 'Project Cfg: ', name
    temp = __import__(name, globals(), locals(), ['settings'], -1)
    # print temp
    settings = getattr(temp, 'settings')
    print settings
    gen_db3s(settings)

if __name__ == '__main__':
    main()
