package com.argo.web.controllers.acl;

import javax.validation.constraints.NotNull;

/**
 * Created by yaming_deng on 2014/10/8.
 */
public class AssignForm {

    @NotNull(message = "roleId_empty")
    private Integer roleId;

    @NotNull(message = "itemId_empty")
    private Integer itemId;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
}
