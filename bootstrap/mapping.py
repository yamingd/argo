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

    'datetime': 'int64',
    'date': 'int64',
    'timestamp': 'int64',
    'time': 'int64'
}

ios_types = {
    'int': 'int',
    'tinyint': 'int',
    'smallint': 'int',
    'mediumint': 'int',
    'bigint': 'long',

    'float': 'float',
    'decimal': 'float',
    'double': 'double',

    'text': 'NSString',
    'varchar': 'NSString',

    'datetime': 'NSDate',
    'date': 'NSDate',
    'timestamp': 'NSDate',
    'time': 'NSDate'
}

sqlite_types = {
    'int': 'integer',
    'tinyint': 'integer',
    'smallint': 'integer',
    'mediumint': 'integer',
    'bigint': 'integer',

    'float': 'real',
    'decimal': 'real',
    'double': 'real',

    'text': 'text',
    'varchar': 'text',

    'datetime': 'integer',
    'date': 'integer',
    'timestamp': 'integer',
    'time': 'integer'
}

cpp_types = {
    'int': 'uint32_t',
    'tinyint': 'uint32_t',
    'smallint': 'uint32_t',
    'mediumint': 'uint32_t',
    'bigint': 'uint64_t',

    'float': 'float',
    'decimal': 'float',
    'double': 'double',

    'text': 'std::string',
    'varchar': 'std::string',

    'datetime': 'uint64_t',
    'date': 'uint64_t',
    'timestamp': 'uint64_t',
    'time': 'uint64_t'
}

cpp_objcs = {
    'int': 'cppUInt32ToInt',
    'tinyint': 'cppUInt32ToInt',
    'smallint': 'cppUInt32ToInt',
    'mediumint': 'cppUInt32ToInt',
    'bigint': 'cppUInt64ToLong',

    'float': 'cppFloatToFloat',
    'decimal': 'cppFloatToFloat',
    'double': 'cppDoubleToDouble',

    'text': 'cppStringToObjc',
    'varchar': 'cppStringToObjc',

    'datetime': 'cppDateToObjc',
    'date': 'cppDateToObjc',
    'timestamp': 'cppDateToObjc',
    'time': 'cppDateToObjc'
}

objc_cpps = {
    'int': 'cppUInt32ToInt',
    'tinyint': 'cppUInt32ToInt',
    'smallint': 'cppUInt32ToInt',
    'mediumint': 'cppUInt32ToInt',
    'bigint': 'cppUInt64ToLong',

    'float': 'cppFloatToFloat',
    'decimal': 'cppFloatToFloat',
    'double': 'cppDoubleToDouble',

    'text': 'objcStringToCpp',
    'varchar': 'objcStringToCpp',

    'datetime': 'objcDateToCpp',
    'date': 'objcDateToCpp',
    'timestamp': 'objcDateToCpp',
    'time': 'objcDateToCpp'
}