package com.company._project_.web.controllers.sample;

import org.msgpack.annotation.MessagePackMessage;

import java.io.Serializable;
import java.util.Date;

@MessagePackMessage
public class DemoJson implements Serializable {

    private Long id;
    private String name;
    private Date createAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
}
