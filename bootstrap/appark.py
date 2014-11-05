#!/usr/bin/env python
# -*- coding: utf-8 -*-

settings = {
    '_project_': 'appark',
    '_Project_': 'Appark',
    '_company_': 'inno',
    'company': 'inno',
    'Company': 'Inno',
    '_output_': 'E:\\stuff\\gen',
    '_mysql_': {
        'host': '127.0.0.1',
        'port': 3306,
        'user': 'root',
        'passwd': '123456'
    },
    '_order_': ['user', 'product', 'feed', 'log'],
    '_modules_': {
        'product': {
            'db': 'appark',
            'tables': ['app'],
            'ref': []
        },
        'feed': {
            'db': 'appark',
            'tables': ['feed', 'user_feed'],
            'ref': []
        },
        'log': {
            'db': 'appark',
            'tables': ['log_user'],
            'ref': []
        },
        'user': {
            'db': 'appark',
            'tables': ['user'],
            'ref': []
        }
    },
    '_mobile_': {
        'feed': {
            'db': 'appark',
            'tables': ['feed', 'user_feed']
        },
        'log': {
            'db': 'appark',
            'tables': ['log_user']
        },
        'user': {
            'db': 'appark',
            'tables': ['user']
        }
    },
    '_pc_': {},
    '_sqlite3_': {}
}
