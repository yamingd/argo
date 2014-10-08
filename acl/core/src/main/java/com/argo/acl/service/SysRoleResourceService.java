package com.argo.acl.service;

import com.argo.acl.SysRoleResource;
import com.argo.db.template.ServiceBase;

import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */
public interface SysRoleResourceService extends ServiceBase<SysRoleResource>  {

    /**
     *
     * @param item
     */
    void remove(SysRoleResource item);

    /**
     * 读取已关联的资源.
     * @param id
     * @return
     */
    List<Integer> findByRole(Long id);
}