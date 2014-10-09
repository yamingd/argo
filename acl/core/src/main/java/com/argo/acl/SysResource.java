package com.argo.acl;

import com.argo.core.annotation.EntityDef;
import com.argo.core.annotation.PK;
import com.argo.core.base.BaseEntity;

import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */
@EntityDef(table = "sys_resource")
public class SysResource extends BaseEntity {
    
    
    /**
     * 
     * 
     */
    @PK("id")
	private Integer id;
    
    /**
     * 权限代号
     * 
     */
    private String name;
    
    /**
     * 权限名称
     * 
     */
    private String title;
    
    /**
     * 权限对应的URL
     * 
     */
    private String url;

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
     * 权限代号
     * 
     */
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * 权限名称
     * 
     */
    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    
    /**
     * 权限对应的URL
     * 
     */
    public String getUrl(){
        return this.url;
    }
    public void setUrl(String url){
        this.url = url;
    }

    public boolean isChecked(List<Integer> resIds){
        if (resIds != null && resIds.contains(this.id)){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "SysResource{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}