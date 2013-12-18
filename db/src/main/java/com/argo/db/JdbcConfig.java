package com.argo.db;

import com.argo.core.configuration.AbstractConfig;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * <pre>
 *     配置文件:
 *     /WEB-INF/resources/dev/jdbc.yaml
 *     /WEB-INF/resources/test/jdbc.yaml
 *     /WEB-INF/resources/prod/jdbc.yaml
 * </pre>
 *
 * User: yamingdeng
 * Date: 13-11-25
 * Time: 下午9:54
 */
public class JdbcConfig extends AbstractConfig {

    private static final String confName = "jdbc";

    public static JdbcConfig current = null;
    private Properties poolProp = null;
    private List servers = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        cfgFile = confName + ".yaml";
        super.afterPropertiesSet();
        JdbcConfig.current = this;
    }

    public Map getPoolConfig(){
        Object val = this.cfg.get("pool");
        if (val == null){
            return null;
        }
        return (Map)val;
    }

    public Properties getPoolProperties(){
        Object val = this.cfg.get("pool");
        if (val == null){
            return null;
        }
        if (this.poolProp != null){
            return this.poolProp;
        }
        Map map = (Map)val;
        poolProp = new Properties();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            poolProp.put(key, map.get(key));
        }
        return poolProp;
    }

    public Map getCommon(){
        Object val = this.cfg.get("common");
        if (val == null){
            return null;
        }
        return (Map)val;
    }

    public Map getServer(String name){
        servers = this.get(List.class, "server");
        if(servers == null){
            return null;
        }
        for(int i=0; i<servers.size(); i++){
            Map temp = (Map)servers.get(i);
            String tname = ObjectUtils.toString(temp.get("name"));
            if(tname.equalsIgnoreCase(name)){
                return temp;
            }
        }
        return null;
    }

    public List getFarms(){
        List fservers = this.get(List.class, "farm");
        return fservers;
    }

    @Override
    public String getConfName() {
        return confName;
    }
}
