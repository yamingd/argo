package com.argo.acl.service.impl;

import com.argo.acl.SysRoleUser;
import com.argo.acl.service.SysRoleUserService;
import com.argo.core.annotation.Model;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;
import com.argo.service.annotation.RmiService;

import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */
@Model(SysRoleUser.class)
@RmiService(serviceInterface=SysRoleUserService.class)
public class SysRoleUserServiceImpl extends BaseServiceImpl<SysRoleUser> implements SysRoleUserService{

    @Override
    public SysRoleUser findById(Long oid) throws EntityNotFoundException {
        return super.findById(oid);
    }

    @Override
    public Long add(SysRoleUser entity) throws ServiceException {
        return super.add(entity);
    }

    @Override
    public boolean update(SysRoleUser entity) throws ServiceException {
        return false;
    }

    @Override
    public boolean remove(Long oid) throws ServiceException {
        return false;
    }

    @Override
    public boolean remove(SysRoleUser item) {
        String sql = "delete from sys_role_user where roleId=? and userId=?";
        return this.jdbcTemplateM.update(sql, item.getRoleId(), item.getUserId()) > 0;
    }

    @Override
    public List<Integer> findByRole(Integer roleId) {
        String sql = "select userId from sys_role_user where roleId=? order by createAt desc";
        return this.jdbcTemplateS.queryForList(sql, Integer.class, roleId);
    }
}