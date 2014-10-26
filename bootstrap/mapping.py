#!/usr/bin/env python
# -*- coding: utf-8 -*-

java_types = {
    'int': 'Integer',
    'tinyint': 'Integer',
    'smallint': 'Integer',
    'mediumint': 'Integer',
    'bigint': 'Long',

    'float': 'Float',
    'decimal': 'Double',
    'double': 'Double',

    'text': 'String',
    'varchar': 'String',

    'datetime': 'Date',
    'date': 'Date',
    'timestamp': 'Date',
    'time': 'Date'
}

protobuf_types = {
    'int': 'int32',
    'tinyint': 'int32',
    'smallint': 'int32',
    'mediumint': 'int32',
    'bigint': 'int64',

    'float': 'float',
    'decimal': 'double',
    'double': 'double',

    'text': 'string',
    'varchar': 'string',

    'datetime': 'int32',
    'date': 'int32',
    'timestamp': 'int32',
    'time': 'int32'
}

ios_types = {
    'int': 'NSNumber',
    'tinyint': 'NSNumber',
    'smallint': 'NSNumber',
    'mediumint': 'NSNumber',
    'bigint': 'NSNumber',

    'float': 'NSNumber',
    'decimal': 'NSNumber',
    'double': 'NSNumber',

    'text': 'NSString',
    'varchar': 'NSString',

    'datetime': 'NSDate',
    'date': 'NSDate',
    'timestamp': 'NSDate',
    'time': 'NSDate'
}