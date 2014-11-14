package com.argo.core.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 分页器
 * Created by Yaming on 2014/11/14.
 */
public class Pagination<T> implements Serializable {

    /**
     * 页码
     */
    private Integer index = 1;
    /**
     * 每页显示记录数
     */
    private Integer size = null;
    /**
     * 开始记录id
     */
    private Long start = 0L;
    /**
     * 记录总数.
     */
    private Integer total = 0;
    /**
     * 记录集合
     */
    private List<T> items;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPages(){
        if (this.size == null || this.size.intValue() == 0){
            return null;
        }
        int t = total / size;
        if (total % size > 0){
            t++;
        }
        return t;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
