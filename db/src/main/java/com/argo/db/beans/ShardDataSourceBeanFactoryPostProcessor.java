package com.argo.db.beans;

import com.argo.db.JdbcConfig;
import com.argo.db.datasource.ShardPooledDataSourceFactoryBean;
import com.argo.db.shard.DataShardSelector;
import com.argo.db.shard.ShardDbDef;
import com.google.common.collect.Maps;
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
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by yaming_deng on 14-8-22.
 */
public class ShardDataSourceBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

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

            List servers = jdbcConfig.get(List.class, "shard");
            if (servers == null || servers.size() == 0){
                throw new Exception("jdbc.yaml dones't find any shard configuration. please check.");
            }

            String dbns = jdbcConfig.get(String.class, "dbns");
            Map<String, ShardDbDef> pool = Maps.newHashMap();

            for(int i=0; i<servers.size(); i++){
                Map server = (Map) servers.get(i);
                String[] dbidx = ObjectUtils.nullSafeToString(server.get("dbidx")).split(",");
                int start = Integer.parseInt(dbidx[0]);
                int end = Integer.parseInt(dbidx[1]);
                for (int j = start; j <= end; j++) {
                    String dbid = String.format(dbns, j);
                    ShardDbDef def = new ShardDbDef(j, ObjectUtils.nullSafeToString(server.get("host")));
                    def.setDbName(dbid);
                    def.setGroup(ObjectUtils.nullSafeToString(server.get("name")));
                    pool.put(dbid, def);
                    this.postAddDataSource(dlbf, def, dbid);
                }
            }

            jdbcConfig.setHolder(pool);
            DataShardSelector.init();

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

    private void postAddDataSource(DefaultListableBeanFactory dlbf, ShardDbDef def, String dbid) {
        String beanName = "DS_" + dbid;

        logger.info("@@@postAddDataSource, dbid=" + dbid);

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ShardPooledDataSourceFactoryBean.class.getName());
        builder.addPropertyValue("dbid", dbid);

        dlbf.registerBeanDefinition(beanName, builder.getBeanDefinition());

        builder = BeanDefinitionBuilder.rootBeanDefinition(DataSourceTransactionManager.class.getName());
        builder.addConstructorArgReference(beanName);
        dlbf.registerBeanDefinition(beanName + "Tx", builder.getBeanDefinition());

        builder = BeanDefinitionBuilder.rootBeanDefinition(JdbcTemplate.class.getName());
        builder.addConstructorArgReference(beanName);
        dlbf.registerBeanDefinition(beanName + "Jt", builder.getBeanDefinition());
    }

}
