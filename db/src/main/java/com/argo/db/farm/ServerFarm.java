package com.argo.db.farm;

import com.argo.core.service.factory.ServiceLocator;
import com.argo.db.JdbcConfig;
import com.argo.db.identity.IdPolicy;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-11-30
 * Time: 下午8:41
 */
public class ServerFarm implements InitializingBean {

    public static ServerFarm current = null;

    private JdbcConfig jdbcConfig;

    private Map<Integer, String> shardMap = null;
    private Map<String, Integer> servers = null;

    private String dbNamePattern = "";
    private String dbEngine = "";
    private IdPolicy idPolicy = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.initMapping();
        ServerFarm.current = this;
    }

    protected void initMapping() {
        this.dbEngine = ObjectUtils.toString(this.jdbcConfig.getCommon().get("type"));
        if (this.idPolicy == null){
            String tid =  ObjectUtils.toString(this.jdbcConfig.getCommon().get("id"));
            if (tid!=null && !"default".equalsIgnoreCase(tid)){
                this.setIdPolicy(ServiceLocator.instance.get(IdPolicy.class, tid));
            }
        }

        shardMap = new HashMap<Integer, String>();
        servers = new HashMap<String, Integer>();
        List list = this.jdbcConfig.getFarms();
        for (int i=0; i<list.size(); i++){
            Map item = (Map)list.get(i);
            this.dbNamePattern = ObjectUtils.toString(item.get("dbname"));
            String ipaddr = ObjectUtils.toString(item.get("server"));
            String[] str = ObjectUtils.toString(item.get("dbidx")).split(",");
            Integer start = Integer.parseInt(str[2]);
            Integer end = Integer.parseInt(str[3]);
            for(int j=start;j<=end;j++){
                shardMap.put(j, ipaddr); //shard db --> server ip
            }
            servers.put(ipaddr, start); // server ip --> default db
        }
    }

    public JdbcConfig getJdbcConfig() {
        return jdbcConfig;
    }

    public void setJdbcConfig(JdbcConfig jdbcConfig) {
        this.jdbcConfig = jdbcConfig;
    }

    /**
     * 是否是分布式.
     * @return
     */
    public boolean isShard(){
        return this.shardMap.size() > 1;
    }

    /**
     * 选择指定的数据库
     * @param shardId
     * @return
     */
    public ShardDbDef getShard(Integer shardId) {
        ShardDbDef db = new ShardDbDef(shardId, this.shardMap.get(shardId));
        db.setDbName(String.format(this.dbNamePattern, shardId));
        db.setEngine(dbEngine);
        return db;
    }

    /**
     * 随机选择一个数据库.
     * @return
     */
    public ShardDbDef selectShard() {

        if(this.shardMap.size()==1){
            ShardDbDef db = new ShardDbDef(1, this.shardMap.get(1));
            db.setDbName(String.format(this.dbNamePattern, 1));
            return db;
        }

        Integer[] ids = this.shardMap.keySet().toArray(new Integer[0]);

        double pos = Math.random() * ids.length;

        Integer id = ids[(int)pos];

        ShardDbDef db = new ShardDbDef(id, this.shardMap.get(id));
        db.setDbName(String.format(this.dbNamePattern, id));
        db.setEngine(dbEngine);
        return db;
    }

    public IdPolicy getIdPolicy() {
        return idPolicy;
    }

    public void setIdPolicy(IdPolicy idPolicy) {
        this.idPolicy = idPolicy;
    }
}
