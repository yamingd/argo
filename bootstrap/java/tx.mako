package com.{{_company_}}.{{_project_}}.{{_module_}}.service;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by $User on {{now.strftime('%Y-%m-%d %H:%M')}}.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Transactional(value="{{_module_}}Tx", rollbackFor=Exception.class)
public @interface {{_tbi_.entityName}}Tx {
}
