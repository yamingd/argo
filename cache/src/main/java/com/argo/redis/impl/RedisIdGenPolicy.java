package com.argo.redis.impl;

import com.argo.core.policy.IdDef;
import com.argo.core.policy.IdGenPolicy;
import com.argo.redis.RedisBuket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by yaming_deng on 14-8-26.
 */
@Component
public class RedisIdGenPolicy implements IdGenPolicy {

    @Autowired
    private RedisBuket redisBuket;

    private static final String NS_PK = "pk";
    protected static final Integer ShardBit = 46;
    protected static final Integer ObjectTypeBit = 10;

    @Override
    public Long generate(String typeName) {
        String key = NS_PK+":"+typeName;
        Long localId =  redisBuket.incr(key, 1);
        return localId;
    }

    @Override
    public IdDef generate(Integer shardId, Integer objectTypeId) {
        String key = NS_PK+":"+objectTypeId;
        Long localId =  redisBuket.incr(key, 1);
        Long fullId = shardId << ShardBit | objectTypeId << ObjectTypeBit | localId;
        return new IdDef(shardId, objectTypeId, localId, fullId);
    }

    @Override
    public IdDef parse(Long uuid) {
        Long shardId = uuid >> ShardBit;
        Long objectTypeId = (uuid - shardId << ShardBit) << ObjectTypeBit;
        Long localId = uuid - shardId << ShardBit - objectTypeId << ObjectTypeBit;
        return new IdDef(shardId.intValue(), objectTypeId.intValue(), localId, uuid);
    }
}
