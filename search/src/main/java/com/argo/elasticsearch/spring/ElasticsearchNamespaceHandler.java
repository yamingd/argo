package com.argo.elasticsearch.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class ElasticsearchNamespaceHandler extends NamespaceHandlerSupport {

        public void init() {
                registerBeanDefinitionParser("client", new ClientBeanDefinitionParser());
        }

}
