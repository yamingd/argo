package com.argo.db.beans;

import com.argo.db.JdbcConfig;
import com.argo.db.datasource.SimplePooledDataSourceFactoryBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.List;
import java.util.Map;

/**
 * Created by yaming_deng on 14-8-22.
 */
public class SimpleDataSourceFactoryPostProcessor implements BeanFactoryPostProcessor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        if (!(beanFactory instanceof DefaultListableBeanFactory)) {
            throw new IllegalStateException(
                    "CustomAutowireConfigurer needs to operate on a DefaultListableBeanFactory");
        }

        DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;

        try {

            JdbcConfig jdbcConfig = new JdbcConfig();
            jdbcConfig.afterPropertiesSet();
            List<String> servers = jdbcConfig.getOrderedKeys();

            for(int i=0; i<servers.size(); i++){
                Map server = jdbcConfig.get(Map.class, servers.get(i));
                this.postAddDataSource(dlbf, servers.get(i), server);
            }

            String idp = JdbcConfig.current.get(String.class, "idpolicy");
            if (StringUtils.isNotBlank(idp)){
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(IdPolicyFactoryBean.class.getName());
                builder.addPropertyReference("current", idp);
                dlbf.registerBeanDefinition("idPolicy", builder.getBeanDefinition());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void postAddDataSource(DefaultListableBeanFactory dlbf, String name, Map server) {
        String beanName = "DS_" + name;

        logger.info("@@@postAddDataSource, dbid=" + beanName);

        //datasource
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(SimplePooledDataSourceFactoryBean.class.getName());
        builder.addPropertyValue("name", name);
        dlbf.registerBeanDefinition(beanName, builder.getBeanDefinition());

        //transaction
        builder = BeanDefinitionBuilder.rootBeanDefinition(DataSourceTransactionManager.class.getName());
        builder.addConstructorArgReference(beanName);
        dlbf.registerBeanDefinition(beanName + "Tx", builder.getBeanDefinition());

        //jdbc template
        builder = BeanDefinitionBuilder.rootBeanDefinition(JdbcTemplate.class.getName());
        builder.addConstructorArgReference(beanName);
        dlbf.registerBeanDefinition(beanName + "Jt", builder.getBeanDefinition());
    }

}
