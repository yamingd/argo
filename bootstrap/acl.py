#!/usr/bin/env python
# -*- coding: utf-8 -*-

settings = {
    '_project_': 'acl',
    '_Project_': 'ACL',
    '_company_': 'argo',
    'company': 'argo',
    'Company': 'Argo',
    '_output_': 'E:\\stuff\\acl',
    '_mysql_': {
        'host': '127.0.0.1',
        'port': 3306,
        'user': 'root',
        'passwd': '123456'
    },
    '_modules_': {
        'acl': {
            'db': 'acl_sys',
            'tables': ['sys_resource', 'sys_role', 'sys_role_resource', 'sys_role_user']
        }
    }
}
