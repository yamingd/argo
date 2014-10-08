package com.argo.acl.service.impl;

import com.argo.acl.SysRoleResource;
import com.argo.acl.service.SysRoleResourceService;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;
import com.argo.service.annotation.RmiService;

import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */
@RmiService(serviceInterface=SysRoleResourceService.class)
public class SysRoleResourceServiceImpl extends BaseServiceImpl<SysRoleResource> implements SysRoleResourceService{

    @Override
    public SysRoleResource findById(Long oid) throws EntityNotFoundException {
        return this.findEntityById(oid);
    }

    @Override
    public Long add(SysRoleResource entity) throws ServiceException {
        return this.addEntity(entity);
    }

    @Override
    public boolean update(SysRoleResource entity) throws ServiceException {
        return false;
    }

    @Override
    public boolean remove(Long oid) throws ServiceException {
        return false;
    }

    @Override
    public void remove(SysRoleResource item) {
        String sql = "delete from sys_role_resource where roleId=? and resourceId=?";
        this.jdbcTemplateM.update(sql, item.getRoleId(), item.getResourceId());
    }

    @Override
    public List<Integer> findByRole(Long id) {
        String sql = "select userId from sys_role_resource where roleId=? order by createAt desc";
        return this.jdbcTemplateS.queryForList(sql, Integer.class, id);
    }
}