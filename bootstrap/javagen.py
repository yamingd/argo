#!/usr/bin/env python
# -*- coding: utf-8 -*-

from jinja2 import Environment, FileSystemLoader

loader = FileSystemLoader('java')
env = Environment(loader=loader, trim_blocks=True)


def serve_template(name, **kwargs):
    template = env.get_template(name)
    strs = template.render(**kwargs)
    return strs.encode('utf8')


def render_entity(fname, **kwargs):
    with open(fname, 'w+') as fw:
        fw.write(serve_template('entity.mako', **kwargs))


def render_service(fname, **kwargs):
    with open(fname, 'w+') as fw:
        fw.write(serve_template('service.mako', **kwargs))


def render_tx(fname, **kwargs):
    with open(fname, 'w+') as fw:
        fw.write(serve_template('tx.mako', **kwargs))


def render_service_impl(fname, **kwargs):
    with open(fname, 'w+') as fw:
        fw.write(serve_template('serviceImpl.mako', **kwargs))


def render_service_test(fname, **kwargs):
    with open(fname, 'w+') as fw:
        fw.write(serve_template('serviceTest.mako', **kwargs))


def render_mapper(fname, **kwargs):
    with open(fname, 'w+') as fw:
        fw.write(serve_template('mappers.mako', **kwargs))


def render_protobuf(fname, **kwargs):
    with open(fname, 'w+') as fw:
        fw.write(serve_template('protobuf_entity.mako', **kwargs))


def render_protobuf_wrapper(fname, **kwargs):
    with open(fname, 'w+') as fw:
        fw.write(serve_template('protobuf_wrapper.mako', **kwargs))


def render_ios(fname, **kwargs):
    with open(fname + ".hh", 'w+') as fw:
        fw.write(serve_template('protobuf_hh_wrapper.mako', **kwargs))

    with open(fname + ".mm", 'w+') as fw:
        fw.write(serve_template('protobuf_mm_wrapper.mako', **kwargs))


def render_ios_service(fname, **kwargs):
    with open(fname + ".h", 'w+') as fw:
        fw.write(serve_template('ios-service-h.mako', **kwargs))

    with open(fname + ".m", 'w+') as fw:
        fw.write(serve_template('ios-service-m.mako', **kwargs))


def render_controller(fname, **kwargs):
    prj = kwargs.pop('_cprj_', 'admin')
    with open(fname, 'w+') as fw:
        if prj == 'pc':
            fw.write(serve_template('controller-pc.mako', **kwargs))
        elif prj == 'mobile':
            fw.write(serve_template('controller-mobile.mako', **kwargs))
        elif prj == 'admin':
            fw.write(serve_template('controller-admin.mako', **kwargs))


def render_controller_test(fname, **kwargs):
    prj = kwargs.pop('_cprj_', 'admin')
    with open(fname, 'w+') as fw:
        if prj == 'pc':
            fw.write(serve_template('controller-pc-test.mako', **kwargs))
        elif prj == 'mobile':
            fw.write(serve_template('controller-mobile-test.mako', **kwargs))
        elif prj == 'admin':
            fw.write(serve_template('controller-admin-test.mako', **kwargs))


def render_jdbc_yaml(fname, **kwargs):
    with open(fname, 'w+') as fw:
        fw.write(serve_template('jdbc.mako', **kwargs))


def render_form(fname, **kwargs):
    prj = kwargs.pop('_cprj_', 'admin')
    with open(fname, 'w+') as fw:
        if prj == 'pc':
            fw.write(serve_template('form-pc.mako', **kwargs))
        elif prj == 'mobile':
            fw.write(serve_template('form-mobile.mako', **kwargs))
        elif prj == 'admin':
            fw.write(serve_template('form-admin.mako', **kwargs))
