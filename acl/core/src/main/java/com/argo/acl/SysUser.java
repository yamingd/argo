package com.argo.acl;

import com.argo.core.annotation.Column;
import com.argo.core.annotation.EntityDef;
import com.argo.core.base.BaseUser;
import org.msgpack.annotation.MessagePackMessage;

import java.util.Date;

/**
 * Created by $User on 2014-10-08 09:58.
 */
@MessagePackMessage
@EntityDef(table = "sys_user")
public class SysUser extends BaseUser {
    /**
     * 角色代号
     * 
     */
    @Column
    private String name;
    
    /**
     * 角色名称
     * 
     */
    @Column
    private String title;

    @Column
    private Integer statusId;

    @Column
    private String email;

    @Column
    private Date createAt;

    @Column
    private Date lastLoginAt;

    @Column
    private String loginIp;

    @Column
    private String hashPasswd;

    @Override
    public String getPK() {
        return  ":" + this.getUid() ;
    }

    
    /**
     * 
     * 
     */
    public Integer getId(){
        return this.getUid().intValue();
    }
    /**
     * 角色代号
     * 
     */
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * 角色名称
     * 
     */
    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getHashPasswd() {
        return hashPasswd;
    }

    public void setHashPasswd(String hashPasswd) {
        this.hashPasswd = hashPasswd;
    }

    @Override
    public String getPasswd() {
        return this.hashPasswd;
    }

    @Override
    public void setPasswd(String passwd) {
        super.setPasswd(passwd);
        this.setHashPasswd(passwd);
    }

    @Override
    public String getLoginId() {
        return this.getName();
    }

    @Override
    public String toString() {
        return "SysUser{" +
                "id=" + getUid() +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", statusId=" + statusId +
                ", email='" + email + '\'' +
                '}';
    }
}