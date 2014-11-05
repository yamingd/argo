package com.argo.core.base;

import java.util.List;

/**
 * Created by Yaming on 2014/11/5.
 */
public interface CacheBucket {

    /**
     * 写入缓存. key存在的话用新值覆盖.
     * @param key
     * @param value
     * @return
     */
    <T> boolean put(String key, T value);
    <T> boolean put(String key, T value, int expireSeconds);
    /**
     * 移除缓存
     * @param key
     * @return
     */
    boolean remove(String key);
    /**
     * 读取字符串缓存
     * @param key
     * @return
     */
    String gets(String key);
    List<String> gets(String[] key);
    /**
     * 读取缓存
     * @param clazz: 返回记录类型
     * @param key
     * @param <T>
     * @return
     */
    <T> T geto(final Class<T> clazz, String key);
    /**
     * 返回多个Key的值
     * @param clazz
     * @param key
     * @param <T>
     * @return
     */
    <T> List<T> geto(final Class<T> clazz, String[] key);
}
