package com.argo.mail;

import com.argo.core.base.BaseEntity;

import java.util.List;

/**
 * Created by yaming_deng on 14-8-27.
 */
public class EmailItem extends BaseEntity {

    private Long id;
    private String templateName;
    private Long fromUserId;
    private List<Long> toUserIds;
    private List<Long> ccUserIds;
    private List<Long> bccUserIds;
    private List<Long> fileIds;
    private List<Long> objectIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public List<Long> getToUserIds() {
        return toUserIds;
    }

    public void setToUserIds(List<Long> toUserIds) {
        this.toUserIds = toUserIds;
    }

    public List<Long> getCcUserIds() {
        return ccUserIds;
    }

    public void setCcUserIds(List<Long> ccUserIds) {
        this.ccUserIds = ccUserIds;
    }

    public List<Long> getBccUserIds() {
        return bccUserIds;
    }

    public void setBccUserIds(List<Long> bccUserIds) {
        this.bccUserIds = bccUserIds;
    }

    public List<Long> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<Long> fileIds) {
        this.fileIds = fileIds;
    }

    public List<Long> getObjectIds() {
        return objectIds;
    }

    public void setObjectIds(List<Long> objectIds) {
        this.objectIds = objectIds;
    }

    @Override
    public String getPK() {
        return this.id + "";
    }
}
