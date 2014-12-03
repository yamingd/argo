#!/usr/bin/env python
# -*- coding: utf-8 -*-

settings = {
    '_project_': 'acl',
    '_Project_': 'ACL',
    '_company_': 'argo',
    'company': 'argo',
    'Company': 'Argo',
    '_output_': 'E:\\stuff\\acl',
    '_dburl_': 'mysql://root:123456@127.0.0.1:3306/%s?charset=utf8',
    '_modules_': {
        'acl': {
            'db': 'acl_sys',
            'tables': ['sys_resource', 'sys_role', 'sys_role_resource', 'sys_role_user']
        }
    }
}
