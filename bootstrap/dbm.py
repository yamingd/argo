#!/usr/bin/env python
# -*- coding: utf-8 -*-

from sqlalchemy import create_engine
from sqlalchemy.sql import text
import string
import mapping

db = None
dbtype = 'mysql'


def open(module, settings):
    """
    http://docs.sqlalchemy.org/en/rel_0_9/core/engines.html
    """
    global db, dbtype
    url = settings['_dburl_']  # "mysql://scott:tiger@localhost/test"
    dbtype = url.split(':')[0]
    engine = create_engine(url, echo=True, encoding="utf-8", convert_unicode=True)
    db = engine.connect()
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
        self.comment = row[6]
        self.index = index
        self.lname = self.name.lower()
        if self.comment and self.comment.startswith('@'):
            i = self.comment.index('.')
            self.ref_obj = Table(self.comment[1:i], '')
            self.ref_varName = self.name.replace('Id', '')
            self.ref_type = 'optional'  # single
            if self.name.endswith('s'):
                self.ref_type = 'repeated'  # many
        else:
            self.ref_obj = None

    @property
    def java_type(self):
        tname = self.typeName.split('(')[0]
        return mapping.java_types.get(tname, 'String')

    @property
    def sqlite3_type(self):
        tname = self.typeName.split('(')[0]
        return mapping.sqlite_types.get(tname, 'Text')

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
    def ios_type_ref(self):
        tname = self.typeName.split('(')[0]
        tname = mapping.ios_types.get(tname, '')
        if tname.startswith('NS'):
            return tname + '*'
        return tname

    @property
    def ios_value(self):
        tname = self.typeName.split('(')[0]
        tname = mapping.ios_types.get(tname, '')
        if tname.startswith('NS'):
            return 'self.%s' % self.name
        return '@(self.%s)' % self.name

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
            hint = u'@Length(min=0, max=%s, message="%s_too_long")\n\t' % (
                self.max, name)
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
    def varName(self):
        return java_name(self.name, upperFirst=False)

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

    @property
    def pkCol(self):
        if self.pks and len(self.pks) == 1:
            return self.pks[0]
        return None

    def protobufRefAs(self, kind):
        if kind == 'repeated':
            return 'NSArray*'
        else:
            return 'TS%s*' % self.entityName

    def refImport(self):
        cs = []
        for c in self.refs:
            cs.append(c.ref_obj.entityName)
        cs = list(set(cs))
        return cs

    def mvc_url(self):
        if hasattr(self, 'url'):
            return self.url
        url = self.name
        if url.startswith(self.mname):
            url = url[len(self.mname) + 1:]
        if url.endswith('_'):
            url = url[0:-1]
        url = '/'.join(url.split('_'))
        if url.endswith('/'):
            url = url[0:-1]
        if len(url) > 0:
            url = self.mname + '/' + url
        else:
            url = self.mname
        self.url = url
        return url


def get_mysql_table(module, tbl_name):
    global db
    sql = text(
        "SELECT table_name, table_comment FROM INFORMATION_SCHEMA.tables t WHERE table_schema=:x and table_name=:y")
    rows = db.execute(sql, x=module['db'], y=tbl_name).fetchall()
    tbl = None
    for row in rows:
        tbl = Table(tbl_name, row[1])
        break
    if tbl is None:
        print 'table not found. ', tbl_name
        raise
    sql = text(
        "select column_name,column_type,is_nullable,column_key,column_default,CHARACTER_MAXIMUM_LENGTH,column_comment from INFORMATION_SCHEMA.COLUMNS where table_schema=:x and table_name=:y")
    # cursor = db.cursor()
    # cursor.execute(sql, [module['db'], tbl_name])
    rows = db.execute(sql, x=module['db'], y=tbl_name).fetchall()
    print rows
    cols = []
    pks = []
    refs = []
    index = 0
    for row in rows:
        c = Column(row, index)
        cols.append(c)
        index += 1
        if c.key:
            pks.append(c)
        if c.ref_obj:
            refs.append(c)
    tbl.columns = cols
    tbl.pks = pks
    tbl.refs = refs
    tbl.mname = module['name']
    return tbl


def get_mssql_table(module, tbl_name):
    global db
    sql = text(
        "SELECT table_name, table_comment FROM INFORMATION_SCHEMA.tables t WHERE table_schema=:x and table_name=:y")
    rows = db.execute(sql, x=module['db'], y=tbl_name).fetchall()
    tbl = None
    for row in rows:
        tbl = Table(tbl_name, row[1])
        break
    if tbl is None:
        print 'table not found. ', tbl_name
        raise
    sql = text(
        "select column_name,column_type,is_nullable,column_key,column_default,CHARACTER_MAXIMUM_LENGTH,column_comment from INFORMATION_SCHEMA.COLUMNS where table_schema=:x and table_name=:y")
    # cursor = db.cursor()
    # cursor.execute(sql, [module['db'], tbl_name])
    rows = db.execute(sql, x=module['db'], y=tbl_name).fetchall()
    print rows
    cols = []
    pks = []
    refs = []
    index = 0
    for row in rows:
        c = Column(row, index)
        cols.append(c)
        index += 1
        if c.key:
            pks.append(c)
        if c.ref_obj:
            refs.append(c)
    tbl.columns = cols
    tbl.pks = pks
    tbl.refs = refs
    tbl.mname = module['name']
    return tbl


def get_table(module, tbl_name):
    if dbtype.startswith('mysql'):
        return get_mysql_table(module, tbl_name)
    elif dbtype.startswith('mssql'):
        return get_mssql_table(module, tbl_name)
    

def java_name(tbl_name, suffix=[], upperFirst=True):
    tmp = tbl_name.split('_')
    tmp.extend(suffix)
    if upperFirst:
        tmp = [item[0].upper() + item[1:] for item in tmp]
    return ''.join(tmp)
