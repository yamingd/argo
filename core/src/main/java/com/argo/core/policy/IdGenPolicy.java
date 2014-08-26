package com.argo.core.policy;


/**
 * 
 * 主键产生策略
 *
 * @author yaming_deng
 * @date 2013-1-15
 */
public interface IdGenPolicy {
    /**
     *
     * @param typeName
     * @return
     */
    Long generate(String typeName);
	/**
	 * 产生全局唯一ID
	 * @param objectTypeId
	 * @return
	 */
    IdDef generate(Integer shardId, Integer objectTypeId);
	/**
	 * 解析全局唯一ID
	 * @param uuid
	 * @return
	 */
    IdDef parse(Long uuid);
}
