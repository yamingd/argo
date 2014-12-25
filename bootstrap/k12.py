#!/usr/bin/env python
# -*- coding: utf-8 -*-

settings = {
    '_project_': 'k12',
    '_Project_': 'K12',
    '_company_': 'inno',
    'company': 'inno',
    'Company': 'Inno',
    '_output_': '../gen',
    
    '_order_': ['catalog', 'file', 'society', 'message', 'school', 'community'],
    '_modules_': {
        'catalog': {
            'db': 'k12_society',
            'tables': ['country', 'province', 'city', 'catalog'],
            'ref': []
        },
        'society': {
            'db': 'k12_society',
            'tables': ['family', 'person', 'device',
                       'account', 'social', 'school', 'contact'],
            'ref': []
        },
        'school': {
            'db': 'k12_school',
            'tables': ['class_room', 'class_member', 'class_course',
                       'course', 'course_member', 'homework', 'homework_member', 'homework_comment',
                       'notice', 'notice_member', 'invitation', 'student', 'teacher',
                       'group', 'group_member', 'group_homework'],
            'ref': ['society', 'file']
        },
        'message': {
            'db': 'k12_message',
            'tables': ['chat', 'chat_member', 'chat_message', 'chat_ack', 'sms_message'],
            'ref': ['society', 'file']
        },
        'file': {
            'db': 'k12_file',
            'tables': ['attachment'],
            'ref': []
        },
        'community': {
            'db': 'k12_community',
            'tables': ['forum', 'post', 'post_comment', 'post_content'],
            'ref': ['society', 'file']
        }
    },
    '_mobile_': {
        'catalog': {
            'db': 'k12_society',
            'tables': ['country', 'province', 'city', 'catalog']
        },
        'society': {
            'db': 'k12_society',
            'tables': ['person', 'device', 'account', 'school', 'contact']
        },
        'school': {
            'db': 'k12_school',
            'tables': ['class_room', 'course',
                       'homework', 'homework_member', 'homework_comment',
                       'notice', 'notice_member',
                       'invitation', 'student', 'teacher',
                       'group', 'group_member', 'group_homework']
        },
        'message': {
            'db': 'k12_message',
            'tables': ['chat', 'chat_member', 'chat_message', 'chat_ack']
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
    '_sqlite3_': {}
}
