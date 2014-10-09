package com.argo.acl;

import com.argo.core.base.BaseEntity;
import com.argo.core.annotation.EntityDef;
import com.argo.core.annotation.PK;
import org.msgpack.annotation.MessagePackMessage;

import java.util.Date;

/**
 * Created by $User on 2014-10-08 09:58.
 */
@MessagePackMessage
@EntityDef(table = "sys_role_user")
public class SysRoleUser extends BaseEntity {
    
    
    /**
     * 
     * 
     */
    @PK("roleId")
	private Integer roleId;
    
    /**
     * 
     * 
     */
    @PK("userId")
	private Integer userId;
    
    /**
     * 
     * 
     */
    private Date createAt;
    

    @Override
    public String getPK() {
        return  ":" + roleId + ":" + userId ;
    }

    
    /**
     * 
     * 
     */
    public Integer getRoleId(){
        return this.roleId;
    }
    public void setRoleId(Integer roleId){
        this.roleId = roleId;
    }
    
    /**
     * 
     * 
     */
    public Integer getUserId(){
        return this.userId;
    }
    public void setUserId(Integer userId){
        this.userId = userId;
    }
    
    /**
     * 
     * 
     */
    public Date getCreateAt(){
        return this.createAt;
    }
    public void setCreateAt(Date createAt){
        this.createAt = createAt;
    }

    @Override
    public String toString() {
        return "SysRoleUser{" +
                "roleId=" + roleId +
                ", userId=" + userId +
                ", createAt=" + createAt +
                '}';
    }
}