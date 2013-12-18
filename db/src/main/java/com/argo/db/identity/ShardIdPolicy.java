package com.argo.db.identity;

import com.argo.db.farm.ShardIdDef;

/**
 * 分布式算法来作为主键.
 * 在Application.yaml配置
 * 
 * shards.id.policy : "shardIdPolicy"
 * 
 * @author yaming_deng
 * @date 2013-1-16
 */
public class ShardIdPolicy implements IdPolicy {

	/**
	 * 
	 */
	protected static final Integer ShardBit = 46;
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
		//Integer shardId = DatafarmContext.getContext().getShardId();
		//Long fullId = shardId << ShardBit | objectTypeId << ObjectTypeBit | localId;
		//return new ShardIdDef(shardId, objectTypeId, localId, fullId);
        return null;
	}

	@Override
	public ShardIdDef parse(Long uuid) {
		Long shardId = uuid >> ShardBit;
		Long objectTypeId = (uuid - shardId << ShardBit) << ObjectTypeBit;
		Long localId = uuid - shardId << ShardBit - objectTypeId << ObjectTypeBit;
		return new ShardIdDef(shardId.intValue(), objectTypeId.intValue(), localId, uuid);
	}

}
