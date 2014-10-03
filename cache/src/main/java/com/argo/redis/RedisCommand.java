package com.argo.redis;

import redis.clients.jedis.BinaryJedis;

public interface RedisCommand<T> {
	/**
	 * 执行具体的Redis命令.
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	T execute(final BinaryJedis conn)throws Exception;
}
