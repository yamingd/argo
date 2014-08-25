package com.company.user;

import com.argo.core.base.BaseUser;

import java.util.Date;

/**
 * Created by yaming_deng on 14-8-19.
 */
public class User extends BaseUser {

    private Long recommenderId;
    private String wxId;
    private String qqId;
    private String alipayId;
    private Integer provinceId;
    private Integer cityId;
    private Integer confimred;
    private Date confirmAt;

    public Long getRecommenderId() {
        return recommenderId;
    }

    public void setRecommenderId(Long recommenderId) {
        this.recommenderId = recommenderId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public String getAlipayId() {
        return alipayId;
    }

    public void setAlipayId(String alipayId) {
        this.alipayId = alipayId;
    }

    public String getQqId() {
        return qqId;
    }

    public void setQqId(String qqId) {
        this.qqId = qqId;
    }

    public String getWxId() {
        return wxId;
    }

    public void setWxId(String wxId) {
        this.wxId = wxId;
    }

    public Integer getConfimred() {
        return confimred;
    }

    public void setConfimred(Integer confimred) {
        this.confimred = confimred;
    }

    public Date getConfirmAt() {
        return confirmAt;
    }

    public void setConfirmAt(Date confirmAt) {
        this.confirmAt = confirmAt;
    }

}
