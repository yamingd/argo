#!/usr/bin/env python
# -*- coding: utf-8 -*-

settings = {
    '_project_': 'park',
    '_Project_': 'Park',
    '_company_': 'inno',
    'company': 'inno',
    'Company': 'Inno',
    '_output_': 'E:\\stuff\\gen',
    '_dburl_': 'mysql://root:123456@127.0.0.1:3306/INFORMATION_SCHEMA?charset=utf8',
    '_order_': ['catalog', 'system', 'person', 'company', 'news', 'proposal', 'project', 'expert'],
    '_modules_': {
        'catalog': {
            'db': 'inno_park',
            'tables': ['catalog_bizarea', 'catalog_biztype', 'catalog_degree',
                        'catalog_emp','catalog_person','catalog_relation',
                        'catalog_spendtype','catalog_stage', 'catalog_jobtype',
                        'catalog_level', 'catalog_college', 'catalog_major'],
            'ref': []
        },
        'proposal': {
            'db': 'inno_park',
            'tables': ['field', 'permission', 'proposal',
                        'proposal_detail', 'proposal_ext', 'proposal_file',
                        'proposal_member', 'proposal_notice', 'workflow', 'workflow_approve'
                        ],
            'ref': []
        },
        'project': {
            'db': 'inno_park',
            'tables': ['project', 'outcome', 'outcome_file', 'spend', 'spend_file'],
            'ref': []
        },
        'expert': {
            'db': 'inno_park',
            'tables': ['expert', 'expert_skill', 'expert_work'],
            'ref': []
        },
        'person': {
            'db': 'inno_park',
            'tables': ['person', 'person_family', 'person_school'],
            'ref': []
        },
        'company': {
            'db': 'inno_park',
            'tables': ['company', 'company_member', 'company_event', 'financy', 'financy_item'],
            'ref': []
        },
        'news': {
            'db': 'inno_park',
            'tables': ['kit', 'post', 'post_content', 'post_file'],
            'ref': []
        },
        'system': {
            'db': 'inno_park',
            'tables': ['sys_user'],
            'ref': []
        }
    },
    '_mobile_': {
    },
    '_pc_': {
        'news': {
            'db': 'inno_park',
            'tables': ['kit', 'post']
        },
        'proposal': {
            'db': 'inno_park',
            'tables': ['proposal']
        },
        'project': {
            'db': 'inno_park',
            'tables': ['project']
        },
        'expert': {
            'db': 'inno_park',
            'tables': ['expert']
        },
        'person': {
            'db': 'inno_park',
            'tables': ['person']
        },
    }
}
