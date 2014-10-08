package com.argo.acl.service.impl;

import com.argo.acl.AclMappers;
import com.argo.acl.SysResource;
import com.argo.acl.service.SysResourceService;
import com.argo.acl.service.SysResourceTx;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;
import com.argo.service.annotation.RmiService;

import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */
@RmiService(serviceInterface=SysResourceService.class)
public class SysResourceServiceImpl extends BaseServiceImpl<SysResource> implements SysResourceService{

    @Override
    public SysResource findById(Long oid) throws EntityNotFoundException {
        return this.findEntityById(oid);
    }

    @SysResourceTx
    @Override
    public Long add(SysResource entity) throws ServiceException {
        return this.addEntity(entity);
    }

    @SysResourceTx
    @Override
    public boolean update(SysResource entity) throws ServiceException {
        return false;
    }

    @SysResourceTx
    @Override
    public boolean remove(Long oid) throws ServiceException {
        return this.removeEntity(oid);
    }

    @Override
    public List<SysResource> findAll() {
        String sql = "select * from sys_resource";
        return super.jdbcTemplateS.query(sql, AclMappers.SysResource_ROWMAPPER);
    }

    @Override
    public List<SysResource> findByUser(Long userId) {
        String sql = "select t.* from sys_resource t, sys_role_resource t2, sys_role_user t3 where t.id=t2.resourceId and t2.roleId=t3.roleId and t3.userId=?";
        return super.jdbcTemplateS.query(sql, AclMappers.SysResource_ROWMAPPER, userId);
    }
}