#!/usr/bin/env python
# -*- coding: utf-8 -*-

settings = {
    '_project_': 'career',
    '_Project_': 'Career',
    '_company_': 'inno',
    'company': 'inno',
    'Company': 'Inno',
    '_output_': '../gen',
    '_dburl_': 'mysql://career:career@123@120.24.225.106:33060/%s?charset=utf8',
    '_order_': ['catalog', 'system', 'member', 'activity', 'article', 'survey', 'wx'],
    '_modules_': {
        'catalog': {
            'db': 'career_dev',
            'tables': ['catalog'],
            'ref': []
        },
        'system': {
            'db': 'career_dev',
            'tables': ['log_op', 'log_email', 'attachment', 'sys_menu'],
            'ref': []
        },
        'member': {
            'db': 'career_dev',
            'tables': ['user', 'user_authority', 'personal', 'personal_work', 'company'],
            'ref': []
        },
        'activity': {
            'db': 'career_dev',
            'tables': ['activity', 'activity_member', 'activity_file'],
            'ref': []
        },
        'survey': {
            'db': 'career_dev',
            'tables': ['survey', 'survey_answer', 'survey_item', 'survey_option'],
            'ref': []
        },
        'article': {
            'db': 'career_dev',
            'tables': ['article', 'kit'],
            'ref': []
        },
        'wx': {
            'db': 'career_dev',
            'tables': ['wx_menu'],
            'ref': []
        }
    },
    '_mobile_': {
        'activity': {
            'db': 'career_dev',
            'tables': ['activity', 'activity_member'],
            'ref': []
        },
        'survey': {
            'db': 'career_dev',
            'tables': ['survey', 'survey_answer'],
            'ref': []
        },
        'article': {
            'db': 'career_dev',
            'tables': ['article', 'kit'],
            'ref': []
        },
        'member': {
            'db': 'career_dev',
            'tables': ['user', 'personal', 'personal_work', 'company'],
            'ref': []
        },
        'wx': {
            'db': 'career_dev',
            'tables': ['wx_menu'],
            'ref': []
        }
    },
    '_pc_': {
        'activity': {
            'db': 'career_dev',
            'tables': ['activity', 'activity_member'],
            'ref': []
        },
        'survey': {
            'db': 'career_dev',
            'tables': ['survey', 'survey_answer'],
            'ref': []
        },
        'article': {
            'db': 'career_dev',
            'tables': ['article', 'kit'],
            'ref': []
        },
        'member': {
            'db': 'career_dev',
            'tables': ['user', 'personal', 'personal_work', 'company'],
            'ref': []
        },
        'catalog': {
            'db': 'career_dev',
            'tables': ['catalog'],
            'ref': []
        }
    }
}
