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

    def __init__(self, row, index):
        self.name = row[0]  # column_name
        self.typeName = row[1]  # column_type
        self.null = row[2] == 'YES'  # is_nullable
        self.key = row[3] and row[3] == 'PRI'  # column_key
        self.default = row[4] if row[4] else u''  # column_default
        self.max = row[5] if row[5] else None
        self.comment = row[-1]
        self.index = index
        self.lname = self.name.lower()

    @property
    def java_type(self):
        tname = self.typeName.split('(')[0]
        return mapping.java_types.get(tname, 'String')
    
    @property
    def protobuf_value(self):
        if self.java_type == 'Date':
            return '.getTime()'
        return ''

    @property
    def protobuf_type(self):
        tname = self.typeName.split('(')[0]
        return mapping.protobuf_types.get(tname, 'string')
    
    @property
    def protobuf_name(self):
        if self.lname in ['typeid', 'typename']:
            return self.lname + '_'
        return self.lname

    @property
    def ios_type(self):
        tname = self.typeName.split('(')[0]
        return mapping.ios_types.get(tname, '')
    
    @property
    def cpp_type(self):
        tname = self.typeName.split('(')[0]
        return mapping.cpp_types.get(tname, '')
    
    @property
    def cpp_objc(self):
        tname = self.typeName.split('(')[0]
        return mapping.cpp_objcs.get(tname, '')

    @property
    def objc_cpp(self):
        tname = self.typeName.split('(')[0]
        return mapping.objc_cpps.get(tname, '')

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

    @property
    def isString(self):
        return self.typeName.startswith('varchar') or self.typeName.startswith('text')
    
    @property
    def validate(self):
        if self.null and self.max is None:
            return u''
        hint = u''
        name = java_name(self.name, upperFirst=False)
        if self.max:
            hint = u'@Length(min=0, max=%s, message="%s_too_long")\n\t' % (self.max, name)
        if not self.null:
            hint = '@NotEmpty(message="%s_empty")\n\t%s' % (name, hint)
        return hint


class Table(object):

    """docstring for Table"""

    def __init__(self, name, hint):
        self.name = name
        self.hint = hint
        self.pks = []

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
    
    @property
    def pkType(self):
        if self.pks:
            return self.pks[0].java_type
        return ''


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
    sql = "select column_name,column_type,is_nullable,column_key,column_default,CHARACTER_MAXIMUM_LENGTH,column_comment from INFORMATION_SCHEMA.COLUMNS where table_schema=%s and table_name=%s"
    cursor = db.cursor()
    cursor.execute(sql, [module['db'], tbl_name])
    cols = []
    pks = []
    index = 0
    for row in cursor.fetchall():
        c = Column(row, index)
        cols.append(c)
        index += 1
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


def java_name(tbl_name, suffix=[], upperFirst=True):
    tmp = tbl_name.split('_')
    tmp.extend(suffix)
    if upperFirst:
        tmp = [item[0].upper() + item[1:] for item in tmp]
    return ''.join(tmp)
