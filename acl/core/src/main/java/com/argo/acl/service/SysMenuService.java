package com.argo.acl.service;

import com.argo.acl.SysMenu;
import com.argo.db.template.ServiceBase;

import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */
public interface SysMenuService extends ServiceBase  {

    /**
     * 读取所有的用户
     * @return
     */
    List<SysMenu> findAll();

    /**
     * 读取用户的菜单
     * @param sysUserId
     * @return
     */
    List<SysMenu> findByUserId(Integer sysUserId);
}