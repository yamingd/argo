package com.argo.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
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

    private Integer pages;

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

        if (this.size == null || this.size.intValue() == 0){
            this.pages = 0;
        }else{
            int t = total / size;
            if (total % size > 0){
                t++;
            }
            this.pages = t;
        }
    }

    public Integer getPages(){
        return this.pages;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    /**
     * 还有下一页
     * @return
     */
    public boolean hasNext(){
        return this.pages - this.index > 0;
    }

    /**
     * 有前一页
     * @return
     */
    public boolean hasPrev(){
        return this.index > 1 && this.pages > 1;
    }

    /**
     * 显示出来的页码
     * @return
     */
    public List<Integer> getCards(){
        int start = this.index - 2;
        int end = this.index + 2;
        if (start <=0){
            start = 1;
        }
        if (end >= this.pages){
            end = this.pages;
        }
        List<Integer> tmp = new ArrayList<Integer>();
        for(int i=start; i<=end; i++){
            tmp.add(i);
        }
        return tmp;
    }
}
