package com.argo.redis;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("redisBuket")
public class RedisBuket extends RedisTemplate {
	
	public List<String> mget(final String... keys){
		return this.execute(new RedisCommand<List<String>>(){
			public List<String> execute(final Jedis conn) throws Exception {
				return conn.mget(keys);
			}
		});
	}
	
	public String get(final String key){
		return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				return conn.get(key);
			}
		});
	}
	
	public String getSet(final String key, final String value){
		return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				return conn.getSet(key, value);
			}
		});
	}

    public Long setnx(final String key, final String value){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.setnx(key, value);
			}
		});
    }

    /**
     * 相当于Set+Expire命令组合.
     * @param key
     * @param seconds
     * @param value
     * @return
     */
    public String setex(final String key, final int seconds, final String value){
    	return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				return conn.setex(key, seconds, value);
			}
		});
    }
    
	/**
	 * 设置过期时间.
	 * @param key 缓存Key.
	 * @param timeout 缓存时长.
	 * @return 返回设置的timeout
	 */
	public long expire(final String key, final int timeout){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.expire(key, timeout);
			}
		});
	}
	
	/**
	 * 检查Key是否存在.
	 * @param key 缓存Key.
	 * @return 返回true or false.
	 */
	public boolean exists(final String key){
		return this.execute(new RedisCommand<Boolean>(){
			public Boolean execute(final Jedis conn) throws Exception {
				return conn.exists(key);
			}
		});
	}
	/**
	 * 删除Key
	 * @param key 缓存keys
	 * @return
	 */
	public boolean delete(final String... key){
		return this.execute(new RedisCommand<Boolean>(){
			public Boolean execute(final Jedis conn) throws Exception {
				return conn.del(key) > 0;
			}
		});
	}
	/**
	 * 自增
	 * @param key
	 * @param amount
	 * @return
	 */
	public long incr(final String key, final Integer amount){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.incrBy(key, amount);
			}
		});
	}
	/**
	 * 自减
	 * @param key
	 * @param amount
	 * @return
	 */
	public long decr(final String key, final Integer amount){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.decrBy(key, amount);
			}
		});
	}
	/**
	 * HashMap自增
	 * @param key
	 * @param amount
	 * @return
	 */
	public long hincr(final String key, final String field, final Integer amount){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.hincrBy(key, field, amount);
			}
		});
	}
	/**
	 * HashMap自增.
	 * @param key
	 * @param nums
	 * @return
	 */
	public long hincr(final String key, final Map<String, Integer> nums){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				Pipeline pipe = conn.pipelined();
				for(String field : nums.keySet()){
					pipe.hincrBy(key, field, nums.get(field));
				}
				pipe.exec();
				return 1L;
			}
		});
	}
	/**
	 * HashMap自减
	 * @param key
	 * @param amount
	 * @return
	 */
	public long hdecr(final String key, final String field, final Integer amount){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.hincrBy(key, field, -1 * amount);
			}
		});
	}
	/**
	 * HashMap自增.
	 * @param key
	 * @param nums
	 * @return
	 */
	public long hdecr(final String key, final Map<String, Integer> nums){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				Pipeline pipe = conn.pipelined();
				for(String field : nums.keySet()){
					pipe.hincrBy(key, field, -1 * nums.get(field));
				}
				pipe.exec();
				return 1L;
			}
		});
	}
	/**
	 * 返回HashMap的K-V值.
	 * @param key
	 * @return
	 */
	public Map<String, String> hall(final String key){
		return this.execute(new RedisCommand<Map<String, String>>(){
			public Map<String, String> execute(final Jedis conn) throws Exception {
				Map<String, String> vals = conn.hgetAll(key);
				return vals;
			}
		});
	}
	/**
	 * 移除HashMap的Keys.
	 * @param key
	 * @param fields
	 * @return
	 */
	public boolean hrem(final String key, final String... fields){
		return this.execute(new RedisCommand<Boolean>(){
			public Boolean execute(final Jedis conn) throws Exception {
				return conn.hdel(key, fields) > 0;
			}
		});
	}
	/**
	 * HashMap重置.
	 * @param key
	 * @param nums
	 * @return
	 */
	public long hset(final String key, final Map<String, Integer> nums){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				Pipeline pipe = conn.pipelined();
				for(String field : nums.keySet()){
					pipe.hset(key, field, nums.get(field)+"");
				}
				pipe.exec();
				return 1L;
			}
		});
	}
	
	/**
	 * 从队列右边写入值.
	 * @param key
	 * @param values
	 * @return
	 */
	public Long rpush(final String key, final String... values){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.rpush(key, values);
			}
		});
	}

    /**
     * 从队列左边写入值.
     * @param key
     * @param values
     * @return
     */
    public Long lpush(final String key, final String... values){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.lpush(key, values);
			}
		});
    }

    /**
     * 计算队列的长度.
     * @param key
     * @return
     */
    public Long llen(final String key){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.llen(key);
			}
		});
    }

    /**
     * 分页获取队列的元素.
     * @param key 缓存Key
     * @param page 页码
     * @param limit 每页记录数.
     * @return
     */
    public List<String> lrange(final String key, final int page, final int limit){
    	return this.execute(new RedisCommand<List<String>>(){
			public List<String> execute(final Jedis conn) throws Exception {
				long start = (page - 1) * limit;
				long end = start + limit;
				return conn.lrange(key, start, end);
			}
		});
    }

    /**
     * 清除[start,end)外的元素.
     * @param key
     * @param start
     * @param end
     * @return
     */
    public String ltrim(final String key, final int start, final int end){
    	return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				return conn.ltrim(key, start, end);
			}
		});
    }

    /**
     * 返回第index的元素.
     * @param key
     * @param index
     * @return
     */
    public String lindex(final String key, final int index){
    	return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				return conn.lindex(key, index);
			}
		});
    }

    /**
     * 重置第index的元素值.
     * @param key
     * @param index
     * @param value
     * @return
     */
    public String lset(final String key, final int index, final String value){
    	return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				return conn.lset(key, index, value);
			}
		});
    }

    /**
     * 删除List的某个元素.
     * @param key
     * @param value
     * @return
     */
    public Long lrem(final String key, final String value){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.lrem(key, 0, value);
			}
		});
    }

    /**
     * 从List左边取出元素.(FIFO)
     * @param key
     * @return
     */
    public String lpop(final String key){
    	return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				return conn.lpop(key);
			}
		});
    }

    /**
     * 从List右边取出元素.(LIFO)
     * @param key
     * @return
     */
    public String rpop(final String key){
    	return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				return conn.rpop(key);
			}
		});
    }
    /**
     * 将List中的元素排序.
     * @param key
     * @return
     */
    public List<String> lsort(final String key){
    	return this.execute(new RedisCommand<List<String>>(){
			public List<String> execute(final Jedis conn) throws Exception {
				return conn.sort(key);
			}
		});
    }
    
    public Long lpushx(final String key, final String... values){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.lpushx(key, values);
			}
		});
    }
    
    public Long rpushx(final String key, final String... values){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.rpushx(key, values);
			}
		});
    }

    public List<String> blpop(final int timeout, final String... keys){
    	return this.execute(new RedisCommand<List<String>>(){
			public List<String> execute(final Jedis conn) throws Exception {
				return conn.blpop(timeout, keys);
			}
		});
    }

    public List<String> brpop(final int timeout, final String... keys){
    	return this.execute(new RedisCommand<List<String>>(){
			public List<String> execute(final Jedis conn) throws Exception {
				return conn.brpop(timeout, keys);
			}
		});
    }
    
    /**
     * 往Set结构中写入值.
     * @param key
     * @param members
     * @return
     */
    public Long sadd(final String key, final String... members){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.sadd(key, members);
			}
		});
    }
    
    /**
     * 往Set结构中写入值.
     * @param key
     * @param members
     * @return
     */
    public Long sadd(final String key, final List<?> members){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				Pipeline pipe = conn.pipelined();
				for(Object v : members){
					pipe.sadd(key, String.valueOf(v));
				}
				pipe.exec();
				return 1L;
			}
		});
    }
    
    /**
     * 返回Set结构中的所有元素.
     * @param key
     * @return
     */
    public Set<String> smembers(final String key){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final Jedis conn) throws Exception {
				return conn.smembers(key);
			}
		});
    }

    /**
     * 移除Set结构中的元素.
     * @param key
     * @param members
     * @return
     */
    public Long srem(final String key, final String... members){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.srem(key, members);
			}
		});
    }

    /**
     * 随机移除Set中的元素.
     * @param key
     * @return
     */
    public String spop(final String key){
    	return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				return conn.spop(key);
			}
		});
    }

    /**
     * 返回Set中的元素个数.
     * @param key
     * @return
     */
    public Long scard(final String key){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.scard(key);
			}
		});
    }

    /**
     * 判断Set中是否包含某元素.
     * @param key
     * @param member
     * @return
     */
    public Boolean sismember(final String key, final String member){
    	return this.execute(new RedisCommand<Boolean>(){
			public Boolean execute(final Jedis conn) throws Exception {
				return conn.sismember(key, member);
			}
		});
    }

    /**
     * 随机选取Set中的元素
     * @param key 缓存Key.
     * @param count 返回元素个数
     * @return
     */
    public List<String> srandmember(final String key, final int count){
    	return this.execute(new RedisCommand<List<String>>(){
			public List<String> execute(final Jedis conn) throws Exception {
				return conn.srandmember(key, count);
			}
		});
    }
    
    /**
     * 将Set中的元素排序.
     * @param key
     * @return
     */
    public List<String> ssort(final String key){
    	return this.execute(new RedisCommand<List<String>>(){
			public List<String> execute(final Jedis conn) throws Exception {
				return conn.sort(key);
			}
		});
    }
    
    /**
     * 往SortedSet中添加元素.
     * @param key
     * @param score
     * @param member
     * @return
     */
    public Long zadd(final String key, final double score, final String member){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zadd(key, score, member);
			}
		});
    }
    
    /**
     * 
     * 往SortedSet中添加元素.
     * @param key
     * @param scoreMembers
     * @return
     */
    public Long zadd(final String key, final Map<Double, String> scoreMembers){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zadd(key, scoreMembers);
			}
		});
    }

    public Set<String> zrange(final String key, final int page, final int limit){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final Jedis conn) throws Exception {
				long start = (page - 1) * limit;
				long end = start + limit;
				return conn.zrange(key, start, end);
			}
		});
    }
    public Set<String> zrevrange(final String key, final int page, final int limit){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final Jedis conn) throws Exception {
				long start = (page - 1) * limit;
				long end = start + limit;
				return conn.zrevrange(key, start, end);
			}
		});
    }
    
    public Long zrem(final String key, final String... member){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zrem(key, member);
			}
		});
    }

    public Double zincrby(final String key, final double score, final String member){
    	return this.execute(new RedisCommand<Double>(){
			public Double execute(final Jedis conn) throws Exception {
				return conn.zincrby(key, score, member);
			}
		});
    }

    /**
     * 按升序排序，取得排名.
     * @param key
     * @param member
     * @return
     */
    public Long zrank(final String key, final String member){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zrank(key, member);
			}
		});
    }

    /**
     * 按降序排序，取得排名.
     * @param key
     * @param member
     * @return
     */
    public Long zrevrank(final String key, final String member){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zrevrank(key, member);
			}
		});
    }
    
    /**
     * 返回SortedSet元素和分数. 按升序排
     * @param key
     * @param page
     * @param limit
     * @return
     */
    public Set<Tuple> zrangeWithScores(final String key, final int page, final int limit){
    	return this.execute(new RedisCommand<Set<Tuple>>(){
			public Set<Tuple> execute(final Jedis conn) throws Exception {
				long start = (page - 1) * limit;
				long end = start + limit;
				return conn.zrangeWithScores(key, start, end);
			}
		});
    }
    
    /**
     * 返回SortedSet元素和分数. 按降序排
     * @param key
     * @param page
     * @param limit
     * @return
     */
    public Set<Tuple> zrevrangeWithScores(final String key, final int page, final int limit){
    	return this.execute(new RedisCommand<Set<Tuple>>(){
			public Set<Tuple> execute(final Jedis conn) throws Exception {
				long start = (page - 1) * limit;
				long end = start + limit;
				return conn.zrevrangeWithScores(key, start, end);
			}
		});
    }
    
    /**
     * 返回SortedSet元素个数.
     * @param key
     * @return
     */
    public Long zcard(final String key){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zcard(key);
			}
		});
    }

    /**
     * 返回某个元素的分数.
     * @param key
     * @param member
     * @return
     */
    public Double zscore(final String key, final String member){
    	return this.execute(new RedisCommand<Double>(){
			public Double execute(final Jedis conn) throws Exception {
				return conn.zscore(key, member);
			}
		});
    }
    
    public Long zcount(final String key, final double min, final double max){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zcount(key, min, max);
			}
		});
    }

    public Long zcount(final String key, final String min, final String max){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zcount(key, min, max);
			}
		});
    }

    /**
     * 返回分数在[min, max]区间内的元素.
     * 仅返回元素.
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<String> zrangeByScore(final String key, final double min, final double max){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final Jedis conn) throws Exception {
				return conn.zrangeByScore(key, min, max);
			}
		});
    }

    public Set<String> zrangeByScore(final String key, final String min, final String max){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final Jedis conn) throws Exception {
				return conn.zrangeByScore(key, min, max);
			}
		});
    }

    public Set<String> zrevrangeByScore(final String key, final double min, final double max){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final Jedis conn) throws Exception {
				return conn.zrevrangeByScore(key, min, max);
			}
		});
    }
    
    public Set<String> zrevrangeByScore(final String key, final String min, final String max){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final Jedis conn) throws Exception {
				return conn.zrevrangeByScore(key, min, max);
			}
		});
    }
    
    /**
     * 返回分数在[min, max]区间内的元素.
     * 返回元素和分数.
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max){
    	return this.execute(new RedisCommand<Set<Tuple>>(){
			public Set<Tuple> execute(final Jedis conn) throws Exception {
				return conn.zrangeByScoreWithScores(key, min, max);
			}
		});
    }

    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min){
    	return this.execute(new RedisCommand<Set<Tuple>>(){
			public Set<Tuple> execute(final Jedis conn) throws Exception {
				return conn.zrangeByScoreWithScores(key, min, max);
			}
		});
    }

    public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max){
    	return this.execute(new RedisCommand<Set<Tuple>>(){
			public Set<Tuple> execute(final Jedis conn) throws Exception {
				return conn.zrangeByScoreWithScores(key, min, max);
			}
		});
    }
    
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min){
    	return this.execute(new RedisCommand<Set<Tuple>>(){
			public Set<Tuple> execute(final Jedis conn) throws Exception {
				return conn.zrevrangeByScoreWithScores(key, min, max);
			}
		});
    }

    /**
     * 删除[start, end]这个区间排名的元素.
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Long zremrangeByRank(final String key, final int start, final int end){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zremrangeByRank(key, start, end);
			}
		});
    }

    public Long zremrangeByScore(final String key, final double start, final double end){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zremrangeByScore(key, start, end);
			}
		});
    }
    
    public Long zremrangeByScore(final String key, final String start, final String end){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zremrangeByScore(key, start, end);
			}
		});
    }
}
