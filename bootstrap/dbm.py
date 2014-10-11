#!/usr/bin/env python
# -*- coding: utf-8 -*-

import MySQLdb
import string
import mapping

db = None


def open(module, settings):
    global db
    db_kwargs = settings['_mysql_']
    db_kwargs['db'] = module['db']
    db_kwargs['charset'] = "utf8"
    db = MySQLdb.connect(**db_kwargs)
    return db


class Column(object):

    """docstring for Column"""

    def __init__(self, row):
        self.name = row[0]  # column_name
        self.typeName = row[1]  # column_type
        self.null = row[2] == 'YES'  # is_nullable
        self.key = row[3] and row[3] == 'PRI'  # column_key
        self.default = row[4] if row[4] else u''  # column_default
        self.comment = row[-1]

    @property
    def java_type(self):
        tname = self.typeName.split('(')[0]
        return mapping.java_types.get(tname, 'String')

    @property
    def capName(self):
        return java_name(self.name)

    @property
    def defaultTips(self):
        if self.default:
            return u'默认为: ' + self.default
        return u''

    @property
    def pkMark(self):
        if self.key:
            return '@PK("' + self.name + '")\n\t'
        return ''


class Table(object):

    """docstring for Table"""

    def __init__(self, name, hint):
        self.name = name
        self.hint = hint

    @property
    def capName(self):
        return java_name(self.name)
    
    @property
    def entityName(self):
        return java_name(self.name)

    @property
    def serviceName(self):
        return self.capName + 'Service'

    @property
    def serviceImplName(self):
        return self.capName + 'ServiceImpl'

    @property
    def controllerName(self):
        return self.capName + 'Controller'


def get_table(module, tbl_name):
    global db
    sql = "SELECT table_name, table_comment FROM INFORMATION_SCHEMA.tables t WHERE table_schema=%s and table_name=%s"
    cursor = db.cursor()
    cursor.execute(sql, [module['db'], tbl_name])
    tbl = None
    for row in cursor.fetchall():
        tbl = Table(tbl_name, row[1])
        break
    if tbl is None:
        print 'table not found. ', tbl_name
        raise
    sql = "select column_name,column_type,is_nullable,column_key,column_default,column_comment from INFORMATION_SCHEMA.COLUMNS where table_schema=%s and table_name=%s"
    cursor = db.cursor()
    cursor.execute(sql, [module['db'], tbl_name])
    cols = []
    pks = []
    for row in cursor.fetchall():
        c = Column(row)
        cols.append(c)
        if c.key:
            pks.append(c)
    tbl.columns = cols
    tbl.pks = pks
    return tbl


def columns(tbl_name):
    """
    http://dev.mysql.com/doc/refman/5.0/en/show-columns.html
    """
    global db
    sql = "show full columns from " + tbl_name
    cursor = db.cursor()
    n = cursor.execute(sql)
    print n
    cols = []
    pks = []
    for row in cursor.fetchall():
        c = Column(row)
        cols.append(c)
        if c.key:
            pks.append(c)
    return cols, pks


def java_name(tbl_name, suffix=[]):
    tmp = tbl_name.split('_')
    tmp.extend(suffix)
    tmp = [item[0].upper() + item[1:] for item in tmp]
    return ''.join(tmp)
