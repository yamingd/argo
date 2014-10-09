package com.argo.acl;

import com.argo.core.annotation.EntityDef;
import com.argo.core.annotation.PK;
import com.argo.core.base.BaseEntity;

/**
 * Created by $User on 2014-10-08 09:58.
 */
@EntityDef(table = "sys_role")
public class SysRole extends BaseEntity {
    
    
    /**
     * 
     * 
     */
    @PK("id")
	private Integer id;
    
    /**
     * 角色代号
     * 
     */
    private String name;
    
    /**
     * 角色名称
     * 
     */
    private String title;
    

    @Override
    public String getPK() {
        return  ":" + id ;
    }

    
    /**
     * 
     * 
     */
    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
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

    @Override
    public String toString() {
        return "SysRole{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}