package com.argo.service.listener;

import com.argo.service.proxy.ServiceClientPoolListener;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by yaming_deng on 14-9-1.
 */
public class ServiceLocalPublisher implements ServicePublishListener {

    @Autowired
    private ServiceClientPoolListener serviceClientPoolListener;

    @Override
    public boolean publish(String name, String url) {
        List<String> urls = Lists.newArrayList();
        urls.add(url);
        serviceClientPoolListener.onServiceChanged(name, urls);
        return true;
    }

    @Override
    public boolean remove(String name, String url) {
        serviceClientPoolListener.removeUrl(name, url);
        return true;
    }

    @Override
    public boolean remove(String name) {
        List<String> urls = Lists.newArrayList();
        serviceClientPoolListener.onServiceChanged(name, urls);
        return true;
    }

}
