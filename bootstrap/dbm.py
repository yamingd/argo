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
        self.name = row[0]
        self.typeName = row[1]
        self.null = row[3]
        self.key = row[4]
        self.default = row[5] if row[5] else u''
        self.extra = row[6] if row[6] else u''
        self.comment = row[8]

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


class Table(object):

    """docstring for Table"""

    def __init__(self, name):
        self.name = name

    @property
    def capName(self):
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
    for row in cursor.fetchall():
        cols.append(Column(row))
    return cols


def java_name(tbl_name, suffix=[]):
    tmp = tbl_name.split('_')
    tmp.extend(suffix)
    tmp = [string.capitalize(item) for item in tmp]
    return ''.join(tmp)
