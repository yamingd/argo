package com.argo.acl.service;

import com.argo.acl.SysRoleUser;
import com.argo.db.template.ServiceBase;

import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */
public interface SysRoleUserService extends ServiceBase  {

    /**
     *
     * @param item
     */
    boolean remove(SysRoleUser item);

    /**
     *
     * @param userId
     * @return
     */
    boolean removeByUser(Integer userId);
    /**
     * 读取已授权的用户id
     * @param roleId
     * @return
     */
    List<Integer> findByRole(Integer roleId);

    /**
     *
     * @param items
     */
    void addBatch(Integer userId, List<Integer> items);
    /**
     * 读取用户的角色
     * @param id
     * @return
     */
    List<Integer> findByUser(Integer id);
}