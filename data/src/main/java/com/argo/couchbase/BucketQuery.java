package com.argo.couchbase;

import com.couchbase.client.protocol.views.Query;

public class BucketQuery extends Query {
	
	/**
	 * 分页设置.
	 * @param page 页码, 从1开始.
	 * @param limit
	 * @return
	 */
	public BucketQuery setPaging(int page, int limit){
		int offset = (page - 1) * limit;
		super.setLimit(limit);
		super.setSkip(offset);
		return this;
	}
	
	@Override
	public BucketQuery setGroup(boolean group) {
		super.setGroup(group);
		if(group){
			super.setReduce(group);
		}
		return this;
	}
}
