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
@EntityDef(table = "sys_role_resource")
public class SysRoleResource extends BaseEntity {
    
    
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
    @PK("resourceId")
	private Integer resourceId;
    
    /**
     * 
     * 
     */
    private Date createAt;
    

    @Override
    public String getPK() {
        return  ":" + roleId + ":" + resourceId ;
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
    public Integer getResourceId(){
        return this.resourceId;
    }
    public void setResourceId(Integer resourceId){
        this.resourceId = resourceId;
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
        return "SysRoleResource{" +
                "roleId=" + roleId +
                ", resourceId=" + resourceId +
                ", createAt=" + createAt +
                '}';
    }
}