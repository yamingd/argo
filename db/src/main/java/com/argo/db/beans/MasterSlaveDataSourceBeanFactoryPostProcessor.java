package com.argo.db.beans;

import com.argo.db.JdbcConfig;
import com.argo.db.MasterSlaveJdbcTemplate;
import com.argo.db.datasource.MasterSlaveDataSourceFactoryBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.List;
import java.util.Map;

/**
 * Created by yaming_deng on 14-8-22.
 */
public class MasterSlaveDataSourceBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

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

            List servers = jdbcConfig.get(List.class, "ms");
            if (servers == null || servers.size() == 0){
                throw new Exception("jdbc.yaml dones't find any farm configuration. please check.");
            }

            for(int i=0; i<servers.size(); i++){
                Map server = (Map) servers.get(i);
                this.postAddDataSource(dlbf, server, MasterSlaveJdbcTemplate.ROLE_MASTER);
                this.postAddDataSource(dlbf, server, MasterSlaveJdbcTemplate.ROLE_SLAVE);
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

    private void postAddDataSource(DefaultListableBeanFactory dlbf, Map server, String role) {
        String beanName = "DS_" + server.get("name") + "_" + role;

        logger.info("@@@postAddDataSource, name=" + server.get("name") + ", role=" + role);

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(MasterSlaveDataSourceFactoryBean.class.getName());
        builder.addPropertyValue("name", server.get("name") + "");
        builder.addPropertyValue("role", role);

        dlbf.registerBeanDefinition(beanName, builder.getBeanDefinition());

        if (MasterSlaveJdbcTemplate.ROLE_MASTER.equalsIgnoreCase(role)) {
            builder = BeanDefinitionBuilder.rootBeanDefinition(DataSourceTransactionManager.class.getName());
            builder.addConstructorArgReference(beanName);
            dlbf.registerBeanDefinition(server.get("name") + "Tx", builder.getBeanDefinition());
        }

    }

}
