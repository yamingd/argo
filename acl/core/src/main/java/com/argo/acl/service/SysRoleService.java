package com.argo.acl.service;

import com.argo.acl.SysRole;
import com.argo.db.template.ServiceBase;

import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */
public interface SysRoleService extends ServiceBase<SysRole>  {

    /**
     * 读取所有的角色
     * @return
     */
    List<SysRole> findAll();

}