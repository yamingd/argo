package com.argo.redis;

import redis.clients.jedis.Jedis;

public interface RedisCommand<T> {
	/**
	 * 执行具体的Redis命令.
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	T execute(final Jedis conn)throws Exception;
}
