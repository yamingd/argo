package com.{{_company_}}.{{_project_}}.{{_module_}}.service.impl;

import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;
import com.argo.core.password.PasswordServiceFactory;
import com.argo.service.annotation.RmiService;
import com.argo.core.annotation.Model;
import com.{{_company_}}.{{_project_}}.service.BaseServiceImpl;
import com.{{_company_}}.{{_project_}}.{{_module_}}.service.{{_entity_}}Service;
import com.{{_company_}}.{{_project_}}.{{_module_}}.{{_entity_}};
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by $User on {{now.strftime('%Y-%m-%d %H:%M')}}.
 */
@Model({{_entity_}}.class)
@RmiService(serviceInterface={{_entity_}}Service.class)
public class {{_entity_}}ServiceImpl extends BaseServiceImpl<{{_entity_}}> implements {{_entity_}}Service{

    @Override
    public {{_entity_}} findById(Long oid) throws EntityNotFoundException {
        return super.findById(oid);
    }

    @Override
    public Long add({{_entity_}} entity) throws ServiceException {
        return super.add(entity);
    }

    @Override
    public boolean update({{_entity_}} entity) throws ServiceException {
        return false;
    }

    @Override
    public boolean remove(Long oid) throws ServiceException {
        return super.remove(oid);
    }

    @Override
    public List<{{_entity_}}> findAll(){
        return null;
    }
}