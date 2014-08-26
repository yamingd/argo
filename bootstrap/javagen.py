#!/usr/bin/env python
# -*- coding: utf-8 -*-

from jinja2 import Environment, FileSystemLoader

loader = FileSystemLoader('java')
env = Environment(loader=loader)


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


def render_mapper(fname, **kwargs):
    with open(fname, 'w+') as fw:
        fw.write(serve_template('mappers.mako', **kwargs))


def render_controller(fname, **kwargs):
    with open(fname, 'w+') as fw:
        fw.write(serve_template('controller.mako', **kwargs))
