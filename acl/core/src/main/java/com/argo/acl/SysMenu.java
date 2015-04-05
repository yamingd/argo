package com.argo.acl;

import com.argo.core.annotation.Column;
import com.argo.core.annotation.EntityDef;
import com.argo.core.annotation.PK;
import com.argo.core.base.BaseEntity;
import org.msgpack.annotation.MessagePackMessage;

import java.util.Date;
import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */
@MessagePackMessage
@EntityDef(table = "sys_menu")
public class SysMenu extends BaseEntity {
    
    /**
     * 
     * 
     */
    @PK("id")
    @Column
	private Integer id;
    /**
     * 角色名称
     * 
     */
    @Column
    private String title;

    @Column
    private Integer parentId;

    @Column
    private String pageUrl;

    @Column
    private Integer orderNo;

    @Column
    private Date createTime;

    @Column
    private Date updateTime;

    @Column
    private Integer ifAdmin;

    private List<SysMenu> subMenus;

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


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIfAdmin() {
        return ifAdmin;
    }

    public void setIfAdmin(Integer ifAdmin) {
        this.ifAdmin = ifAdmin;
    }

    public List<SysMenu> getSubMenus() {
        return subMenus;
    }

    public void setSubMenus(List<SysMenu> subMenus) {
        this.subMenus = subMenus;
    }

    public boolean checked(List<Integer> ids){
        return ids != null && ids.contains(this.getId());
    }

    @Override
    public String toString() {
        return "SysMenu{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", parentId=" + parentId +
                ", pageUrl='" + pageUrl + '\'' +
                ", orderNo=" + orderNo +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", ifAdmin=" + ifAdmin +
                '}';
    }
}