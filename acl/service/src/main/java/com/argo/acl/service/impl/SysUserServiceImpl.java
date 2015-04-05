package com.argo.acl.service.impl;

import com.argo.acl.AclMappers;
import com.argo.acl.SysUser;
import com.argo.acl.service.SysRoleResourceService;
import com.argo.acl.service.SysRoleUserService;
import com.argo.acl.service.SysUserService;
import com.argo.acl.service.SysUserTx;
import com.argo.core.annotation.Model;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;
import com.argo.core.password.PasswordServiceFactory;
import com.argo.service.annotation.RmiService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */
@Model(SysUser.class)
@RmiService(serviceInterface= SysUserService.class)
public class SysUserServiceImpl extends AclBaseServiceImpl implements SysUserService{

    @Autowired
    private SysRoleResourceService sysRoleResourceService;

    @Autowired
    private SysRoleUserService sysRoleUserService;

    @Autowired
    private PasswordServiceFactory passwordServiceFactory;

    @Override
    public SysUser findById(Long oid) throws EntityNotFoundException {
        return super.findById(oid);
    }

    @Override
    @SysUserTx
    public <T> Long add(T entity) throws ServiceException {
        SysUser sysUser = (SysUser)entity;
        sysUser.setCreateAt(new Date());
        Long id =  super.add(sysUser);
        sysUser.setId(id.intValue());
        return id;
    }

    @Override
    public <T> boolean update(T entity) throws ServiceException {
        return this.updateEntity(entity);
    }

    @Override
    @SysUserTx
    public boolean remove(Long oid) throws ServiceException {
        boolean ok = super.remove(oid);
        return ok;
    }

    @Override
    public List<SysUser> findAll() {
        String sql = "select * from sys_user order by id desc";
        return this.jdbcTemplateS.query(sql, AclMappers.SysUser_ROWMAPPER);
    }

    @Override
    @SysUserTx
    public SysUser addUser(SysUser sysUser) throws ServiceException {
        String password = this.passwordServiceFactory.getDefaultService().encrypt(sysUser.getPasswd(), sysUser.getName());
        sysUser.setPasswd(password);

        Long id = this.add(sysUser);
        sysUser.setId(id.intValue());

        return sysUser;
    }

    @Override
    @SysUserTx
    public boolean updateUser(SysUser sysUser) throws ServiceException {
        this.updateEntity(sysUser);

        return true;
    }

    @Override
    @SysUserTx
    public boolean updatePassword(SysUser sysUser, String oldpass) throws ServiceException {
        String password = this.passwordServiceFactory.getDefaultService().encrypt(sysUser.getPasswd(), sysUser.getName());
        sysUser.setPasswd(password);
        return this.updateEntity(sysUser);
    }
}