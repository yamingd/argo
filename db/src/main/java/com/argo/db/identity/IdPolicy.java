package com.argo.db.identity;


import com.argo.db.farm.ShardIdDef;

/**
 * 
 * 主键产生策略
 *  
 * @author yaming_deng
 * @date 2013-1-15
 */
public interface IdPolicy {
	/**
	 * 产生全局唯一ID
	 * @param objectTypeId
	 * @return
	 */
	ShardIdDef generate(Integer objectTypeId);
	/**
	 * 解析全局唯一ID
	 * @param uuid
	 * @return
	 */
	ShardIdDef parse(Long uuid);
}
