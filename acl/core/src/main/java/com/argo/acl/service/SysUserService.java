package com.argo.acl.service;

import com.argo.acl.SysUser;
import com.argo.core.exception.ServiceException;
import com.argo.db.template.ServiceBase;

import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */
public interface SysUserService extends ServiceBase  {

    /**
     * 读取所有的用户
     * @return
     */
    List<SysUser> findAll();

    /**
     * 新增用户
     * @param sysUser
     * @return
     */
    SysUser addUser(SysUser sysUser) throws ServiceException;

    /**
     * 更新用户
     * @param sysUser
     * @return
     */
    boolean updateUser(SysUser sysUser) throws ServiceException;

    /**
     * 更改密码
     * @param sysUser
     * @param oldpass
     * @return
     * @throws ServiceException
     */
    boolean updatePassword(SysUser sysUser, String oldpass) throws ServiceException;
}