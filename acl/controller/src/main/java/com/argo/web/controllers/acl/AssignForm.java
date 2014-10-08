package com.argo.web.controllers.acl;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by yaming_deng on 2014/10/8.
 */
public class AssignForm {

    @NotEmpty(message = "roleId")
    private Integer roleId;

    @NotEmpty(message = "itemId")
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
