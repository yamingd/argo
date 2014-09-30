package com.argo.demo;

import java.util.Date;

/**
 * Created by yaming_deng on 2014/9/30.
 */
public class Person {

    private Long id;
    private String name;
    private Date createAt;
    private Date updateAt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                '}';
    }
}
