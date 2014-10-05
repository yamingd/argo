package com.{{_company_}}.{{_module_}}.service.impl;

import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.password.PasswordServiceFactory;
import com.argo.service.annotation.RmiService;
import com.{{_company_}}.service.BaseServiceImpl;
import com.{{_company_}}.{{_module_}}.service.{{_entity_}}Service;
import com.{{_company_}}.{{_module_}}.{{_entity_}};
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
@RmiService(serviceInterface={{_entity_}}Service.class)
public class {{_entity_}}ServiceImpl extends BaseServiceImpl implements {{_entity_}}Service{
	
	@Override
    protected String getServerName() {
    	//TODO: must set this
        return null;
    }
}