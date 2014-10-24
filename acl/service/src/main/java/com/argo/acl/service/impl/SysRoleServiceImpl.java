package com.argo.acl.service.impl;

import com.argo.acl.AclMappers;
import com.argo.acl.SysRole;
import com.argo.acl.service.SysRoleService;
import com.argo.core.annotation.Model;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;
import com.argo.service.annotation.RmiService;

import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */
@Model(SysRole.class)
@RmiService(serviceInterface=SysRoleService.class)
public class SysRoleServiceImpl extends BaseServiceImpl implements SysRoleService{

    @Override
    public SysRole findById(Long oid) throws EntityNotFoundException {
        return super.findById(oid);
    }

    public Long add(SysRole entity) throws ServiceException {
        Long id =  super.add(entity);
        entity.setId(id.intValue());
        return id;
    }

    public boolean update(SysRole entity) throws ServiceException {
        String sql = "update sys_role set name=?, title=? where id =? ";
        return this.jdbcTemplateM.update(sql, entity.getName(), entity.getTitle(), entity.getId()) > 0;
    }

    @Override
    public boolean remove(Long oid) throws ServiceException {
        return super.remove(oid);
    }

    @Override
    public List<SysRole> findAll() {
        String sql = "select * from sys_role order by name";
        return this.jdbcTemplateS.query(sql, AclMappers.SysRole_ROWMAPPER);
    }
}