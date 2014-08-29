package com.argo.core.service;

/**
 * Created by yaming_deng on 14-8-29.
 */
public interface ServiceLocator {

    String getServiceName(Class<?> clazz);

    @SuppressWarnings("unchecked")
    <T> T get(String serviceName);

    @SuppressWarnings("unchecked")
    <T> T get(Class<T> clazz);

    @SuppressWarnings("unchecked")
    <T> T get(Class<T> clazz, String serviceName);
}
