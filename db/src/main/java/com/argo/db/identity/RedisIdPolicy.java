package com.argo.db.identity;

import com.argo.db.farm.ShardIdDef;

/**
 * 用Redis来作为主键.
 * 在Application.yaml加入
 * 
 * shards.id.policy : "redisIdPolicy"
 * 
 * @author yaming_deng
 * @date 2013-1-16
 */
public class RedisIdPolicy implements IdPolicy {
	
	protected static final Integer ObjectTypeBit = 10;
	
	/**
	 * 
	 */
	private static final String NS_PK = "pk";

	@Override
	public ShardIdDef generate(Integer objectTypeId) {
        //TODO: Primary Key Generate
		//RedisClientService r = ServiceLocator.instance.getService(RedisClientService.class);
		//Long localId =  r.incr(NS_PK, objectTypeId+"");
		//return new ShardIdDef(objectTypeId, localId);
        return null;
	}

	@Override
	public ShardIdDef parse(Long uuid) {
		return new ShardIdDef(null, uuid);
	}
}
