package com.argo.core.web;

import com.argo.core.json.JsonUtil;
import com.google.common.collect.Lists;
import org.msgpack.annotation.MessagePackMessage;

import java.io.IOException;
import java.util.List;

/**
 * Created by yaming_deng on 14-8-20.
 */
@MessagePackMessage
public class BsonResponse extends MvcResponse {

    protected List<byte[]> data;

    public BsonResponse() {
        super();
        this.data = Lists.newArrayList();
    }

    public <T> void add(T o) throws Exception {
        byte[] bytes = JsonUtil.toBytes(o);
        data.add(bytes);
    }

    public <T> void addAll(List<T> list) throws Exception {
        for (T o : list){
            byte[] bytes = JsonUtil.toBytes(o);
            data.add(bytes);
        }
    }

    public List<byte[]> getData() {
        return data;
    }

    public <T> T dataBean(Class<T> clazz) throws IOException {
        if (this.data==null || this.data.size() == 0){
            return null;
        }

        T o = JsonUtil.asT(clazz, this.data.get(0));
        return o;
    }

    public <T> List<T> dataBeans(Class<T> clazz) throws IOException {
        List<T> ol = Lists.newArrayList();
        if (this.data==null || this.data.size() == 0){
            return ol;
        }

        for (byte[] bytes : this.data){
            T o = JsonUtil.asT(clazz, bytes);
            ol.add(o);
        }

        return ol;
    }
}
