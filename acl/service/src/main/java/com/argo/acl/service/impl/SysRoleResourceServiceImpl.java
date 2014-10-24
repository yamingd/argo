package com.argo.acl.service.impl;

import com.argo.acl.SysRoleResource;
import com.argo.acl.service.SysRoleResourceService;
import com.argo.core.annotation.Model;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;
import com.argo.service.annotation.RmiService;

import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */
@Model(SysRoleResource.class)
@RmiService(serviceInterface=SysRoleResourceService.class)
public class SysRoleResourceServiceImpl extends BaseServiceImpl implements SysRoleResourceService{

    @Override
    public SysRoleResource findById(Long oid) throws EntityNotFoundException {
        return null;
    }

    public Long add(SysRoleResource entity) throws ServiceException {
        return super.add(entity);
    }

    public boolean update(SysRoleResource entity) throws ServiceException {
        return false;
    }

    @Override
    public boolean remove(Long oid) throws ServiceException {
        return false;
    }

    @Override
    public boolean remove(SysRoleResource item) {
        String sql = "delete from sys_role_resource where roleId=? and resourceId=?";
        return this.jdbcTemplateM.update(sql, item.getRoleId(), item.getResourceId()) > 0;
    }

    @Override
    public List<Integer> findByRole(Long id) {
        String sql = "select resourceId from sys_role_resource where roleId=? order by createAt desc";
        return this.jdbcTemplateS.queryForList(sql, Integer.class, id);
    }
}