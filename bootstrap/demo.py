#!/usr/bin/env python
# -*- coding: utf-8 -*-

settings = {
    '_project_': 'demo',
    '_Project_': 'Demo',
    '_company_': 'argo',
    'company': 'argo',
    'Company': 'Argo',
    '_output_': 'E:\\stuff\\sample\\gen',
    '_dburl_': 'mysql://root:123456@127.0.0.1:3306/INFORMATION_SCHEMA?charset=utf8',
    '_modules_': {
        'member': {
            'db': 'ec_member',
            'tables': ['invite']
        },
        'score': {
            'db': 'ec_score',
            'tables': ['score', 'event', 'user_score']
        }
    },
    '_mobile_': {
        'member': {
            'db': 'ec_member',
            'tables': ['invite']
        },
        'score': {
            'db': 'ec_score',
            'tables': ['score', 'event']
        }
    },
    '_pc_': {
        'member': {
            'db': 'ec_member',
            'tables': ['invite']
        },
        'score': {
            'db': 'ec_score',
            'tables': ['event']
        }
    }
}
