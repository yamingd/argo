package com.argo.db.shard;

import com.argo.db.JdbcConfig;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by yaming_deng on 14-8-26.
 */
public class DataShardSelector {

    private static AtomicLong shift = new AtomicLong();
    private static List<String> dbids = Lists.newArrayList();

    public static void init(){
        Map dbs = JdbcConfig.current.getHolder();
        for (Object o : dbs.keySet()){
            dbids.add((String)o);
        }
    }

    /**
     * 随机选择一个数据库.
     * @return
     */
    public static ShardDbDef random() {

        Map dbs = JdbcConfig.current.getHolder();

        if(dbs.size()==1){
            ShardDbDef db = (ShardDbDef) dbs.get(dbids.get(0));
            return db;
        }

        long id = shift.incrementAndGet();
        id = id % dbids.size();

        return (ShardDbDef) dbs.get(dbids.get((int)id));
    }
}
