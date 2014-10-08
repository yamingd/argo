package com.argo.acl.service;

import com.argo.acl.SysResource;
import com.argo.db.template.ServiceBase;

import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */
public interface SysResourceService extends ServiceBase<SysResource>  {

    /**
     * 读取所有资源.
     * @return
     */
    List<SysResource> findAll();

    /**
     * 读取用户能访问的所有资源.
     * @param userId
     * @return
     */
    List<SysResource> findByUser(Long userId);
}