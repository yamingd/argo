#!/usr/bin/env python
# -*- coding: utf-8 -*-

settings = {
    '_project_': 'k12',
    '_Project_': 'K12',
    '_company_': 'inno',
    'company': 'inno',
    'Company': 'Inno',
    '_output_': 'I:\\GitRepo\\argo\\gen',
    '_mysql_': {
        'host': '127.0.0.1',
        'port': 3306,
        'user': 'test',
        'passwd': '12333'
    },
    '_modules_': {
        'catalog': {
            'db': 'k12_society',
            'tables': ['country', 'province', 'city', 'catalog']
        },
        'society': {
            'db': 'k12_society',
            'tables': ['family', 'person', 'device',
                       'account', 'social', 'school']
        },
        'school': {
            'db': 'k12_school',
            'tables': ['class_room', 'class_member', 'class_course',
                       'course', 'course_member', 'homework', 'homework_member', 'homework_comment',
                       'notice', 'notice_member', 'invitation', 'student', 'teacher']
        },
        'message': {
            'db': 'k12_message',
            'tables': ['chat', 'chat_member', 'chat_message']
        },
        'file': {
            'db': 'k12_file',
            'tables': ['attachment']
        },
        'community': {
            'db': 'k12_community',
            'tables': ['forum', 'post', 'post_comment', 'post_content']
        }
    },
    '_mobile_': {
        'catalog': {
            'db': 'k12_society',
            'tables': ['country', 'province', 'city', 'catalog']
        },
        'society': {
            'db': 'k12_society',
            'tables': ['person', 'device', 'account', 'school']
        },
        'school': {
            'db': 'k12_school',
            'tables': ['class_room', 'course',
                       'homework', 'homework_member', 'homework_comment',
                       'notice', 'notice_member',
                       'invitation', 'student', 'teacher']
        },
        'message': {
            'db': 'k12_message',
            'tables': ['chat', 'chat_member', 'chat_message']
        },
        'file': {
            'db': 'k12_file',
            'tables': ['attachment']
        },
        'community': {
            'db': 'k12_community',
            'tables': ['forum', 'post']
        }
    },
    '_pc_': {},
    '_sqlite3_': {
        'catalog': {
            'k12_society': ['country', 'province', 'city', 'catalog']
        },
        'profile': {
            'k12_society': ['person', 'account', 'school'],
            'k12_school': ['class_room', 'class_member', 'class_course',
                       'course', 'course_member', 'homework',
                       'homework_member', 'homework_comment',
                       'notice', 'notice_member', 'invitation', 'student', 'teacher'],
            'k12_message': ['chat', 'chat_member', 'chat_message'],
            'k12_file': ['attachment'],
            'k12_community': ['forum', 'post', 'post_comment', 'post_content']
        }
    }
}
