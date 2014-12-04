#!/usr/bin/env python
# -*- coding: utf-8 -*-

settings = {
    '_project_': 'career',
    '_Project_': 'Career',
    '_company_': 'inno',
    'company': 'inno',
    'Company': 'Inno',
    '_output_': 'E:\\stuff\\gen',
    '_dburl_': 'mysql://root:123456@127.0.0.1:3306/%s?charset=utf8',
    '_order_': ['catalog', 'system', 'member', 'activity', 'article', 'survey', 'wx'],
    '_modules_': {
        'catalog': {
            'db': 'inno_career',
            'tables': ['catalog'],
            'ref': []
        },
        'system': {
            'db': 'inno_career',
            'tables': ['log_op', 'log_email', 'attachment'],
            'ref': []
        },
        'member': {
            'db': 'inno_career',
            'tables': ['user', 'user_authority', 'personal', 'personal_work', 'company'],
            'ref': []
        },
        'activity': {
            'db': 'inno_career',
            'tables': ['activity', 'activity_member', 'activity_file'],
            'ref': []
        },
        'survey': {
            'db': 'inno_career',
            'tables': ['survey', 'survey_answer', 'survey_item', 'survey_option'],
            'ref': []
        },
        'article': {
            'db': 'inno_career',
            'tables': ['article', 'kit'],
            'ref': []
        },
        'wx': {
            'db': 'inno_career',
            'tables': ['wx_menu'],
            'ref': []
        }
    },
    '_mobile_': {
        'activity': {
            'db': 'inno_career',
            'tables': ['activity', 'activity_member'],
            'ref': []
        },
        'survey': {
            'db': 'inno_career',
            'tables': ['survey', 'survey_answer'],
            'ref': []
        },
        'article': {
            'db': 'inno_career',
            'tables': ['article', 'kit'],
            'ref': []
        },
        'member': {
            'db': 'inno_career',
            'tables': ['user', 'personal', 'personal_work', 'company'],
            'ref': []
        },
        'wx': {
            'db': 'inno_career',
            'tables': ['wx_menu'],
            'ref': []
        }
    },
    '_pc_': {
        'activity': {
            'db': 'inno_career',
            'tables': ['activity', 'activity_member'],
            'ref': []
        },
        'survey': {
            'db': 'inno_career',
            'tables': ['survey', 'survey_answer'],
            'ref': []
        },
        'article': {
            'db': 'inno_career',
            'tables': ['article', 'kit'],
            'ref': []
        },
        'member': {
            'db': 'inno_career',
            'tables': ['user', 'personal', 'personal_work', 'company'],
            'ref': []
        },
        'catalog': {
            'db': 'inno_career',
            'tables': ['catalog'],
            'ref': []
        }
    }
}
