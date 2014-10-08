#!/usr/bin/env python
# -*- coding: utf-8 -*-

settings = {
    '_project_': 'demo',
    '_Project_': 'Demo',
    '_company_': 'equation',
    'company': 'equation',
    'Company': 'Equation',
    '_output_': 'E:\\stuff\\sample',
    '_mysql_': {
        'host': '127.0.0.1',
        'port': 3306,
        'user': 'root',
        'passwd': '123456'
    },
    '_modules_': {
        'member': {
            'db': 'ec_member',
            'tables': ['invite']
        },
        'score': {
            'db': 'ec_score',
            'tables': ['score', 'event', 'user_score']
        }
    }
}
