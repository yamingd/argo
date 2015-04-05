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
import com.argo.core.web.WebContext;
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
        sysUser.setUid(id);
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
        String sql = "select * from sys_user order by uid desc";
        return this.jdbcTemplateS.query(sql, AclMappers.SysUser_ROWMAPPER);
    }

    @Override
    @SysUserTx
    public SysUser addUser(SysUser sysUser) throws ServiceException {
        sysUser.setName(sysUser.getName().toLowerCase().trim());
        String password = this.passwordServiceFactory.getDefaultService().encrypt(sysUser.getPasswd(), sysUser.getName());
        sysUser.setHashPasswd(password);

        Long id = this.add(sysUser);
        sysUser.setUid(id);

        return sysUser;
    }

    @Override
    @SysUserTx
    public boolean updateUser(SysUser sysUser) throws ServiceException {
        if (null != sysUser.getName()) {
            sysUser.setName(sysUser.getName().trim().toLowerCase());
        }

        this.updateEntity(sysUser);

        return true;
    }

    @Override
    @SysUserTx
    public boolean updatePassword(SysUser sysUser, String oldpass) throws ServiceException {
        String password = this.passwordServiceFactory.getDefaultService().encrypt(sysUser.getPasswd(), sysUser.getName());
        sysUser.setHashPasswd(password);
        return this.updateEntity(sysUser);
    }

    @Override
    public SysUser verifyUserPassword(String userName, String password) {
        SysUser user = null;
        try {
            user = this.findByUserName(userName);
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage() + ", userName=" + userName);
        }
        if (user == null){
            logger.error("Can't find user. userName={}, password={}", userName, password);
            return null;
        }
        return this.verifyUserPassword(user, password);
    }

    @Override
    public SysUser findByUserName(String userName) throws EntityNotFoundException{
        userName = userName.trim().toLowerCase();
        String sql = "select * from sys_user where name = ?";
        List<SysUser> list = this.jdbcTemplateS.query(sql, AclMappers.SysUser_ROWMAPPER, userName);
        if (list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public SysUser verifyUserPassword(SysUser user, String password) {
        boolean flag = passwordServiceFactory.getDefaultService().validate(password, user.getLoginId(), user);
        if (flag){
            SysUser user1 = new SysUser();
            user1.setUid(user.getUid());
            user1.setLoginAt(new Date());
            user1.setLoginIp(WebContext.getContext().getRequestIp());
            try {
                this.updateUser(user1);
            } catch (ServiceException e) {
                logger.error(e.getMessage(), e);
            }
            return user;
        }
        return null;
    }
}