package com.argo.redis;

import com.argo.core.base.CacheBucket;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.Tuple;
import redis.clients.util.SafeEncoder;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("redisBuket")
public class RedisBuket extends RedisTemplate implements CacheBucket {

    public List<String> fromBytes(List<byte[]> lbs) throws UnsupportedEncodingException {
        List<String> ret = Lists.newArrayList();
        if (lbs == null){
            return ret;
        }
        for (byte[] bs : lbs){
            if (bs != null) {
                ret.add(new String(bs, Protocol.CHARSET));
            }
        }
        return ret;
    }

    /**
     * multi get
     * @param keys keys
     * @return List<String>
     */
    public List<String> mget(final String... keys){
		return this.execute(new RedisCommand<List<String>>(){
			public List<String> execute(final BinaryJedis conn) throws Exception {
                List<byte[]> bytes = conn.mget(SafeEncoder.encodeMany(keys));
                List<String> ret = Lists.newArrayList();
                for (byte[] item : bytes){
                    if (item != null) {
                        ret.add(SafeEncoder.encode(item));
                    }
                }
                return ret;
			}
		});
	}

    /**
     * Get Object List
     * @param clazz
     * @param keys
     * @param <T>
     * @return
     */
    public <T> List<T> mget(final Class<T> clazz, final String... keys){
        return this.execute(new RedisCommand<List<T>>(){
            public List<T> execute(final BinaryJedis conn) throws Exception {
                List<byte[]> bytes = conn.mget(SafeEncoder.encodeMany(keys));
                List<T> ret = Lists.newArrayList();
                for (byte[] item : bytes){
                    if (item != null) {
                        ret.add(messagePack.read(item, clazz));
                    }else{
                        ret.add(null);
                    }
                }
                return ret;
            }
        });
    }

    /**
     * get single key
     * @param key key
     * @return String
     */
	public String get(final String key){
		return this.execute(new RedisCommand<String>(){
			public String execute(final BinaryJedis conn) throws Exception {
				byte[] bytes = conn.get(SafeEncoder.encode(key));
                if (bytes == null){
                    return null;
                }
                return SafeEncoder.encode(bytes);
			}
		});
	}

    /**
     * Get Object
     * @param clazz
     * @param key
     * @param <T>
     * @return
     */
    public <T> T get(final Class<T> clazz, final String key){
        return this.execute(new RedisCommand<T>(){
            public T execute(final BinaryJedis conn) throws Exception {
                byte[] bytes = conn.get(SafeEncoder.encode(key));
                if (bytes == null){
                    return null;
                }
                return messagePack.read(bytes, clazz);
            }
        });
    }

    public <T> String set(final String key, final T value){
        return this.execute(new RedisCommand<String>(){
            public String execute(final BinaryJedis conn) throws Exception {
                byte[] ds = messagePack.write(value);
                return conn.set(SafeEncoder.encode(key), ds);
            }
        });
    }

    /**
     * GETSET
     * @param key
     * @param value
     * @return String: old value
     */
	public String getSet(final String key, final String value){
		return this.execute(new RedisCommand<String>(){
			public String execute(final BinaryJedis conn) throws Exception {
				byte[] ret = conn.getSet(SafeEncoder.encode(key), SafeEncoder.encode(value));
                if (ret == null){
                    return null;
                }
                return SafeEncoder.encode(ret);
			}
		});
	}

    public <T> T getSet(final Class<T> clazz, final String key, final T value){
        return this.execute(new RedisCommand<T>(){
            public T execute(final BinaryJedis conn) throws Exception {
                byte[] ds = messagePack.write(value);
                byte[] ret = conn.getSet(SafeEncoder.encode(key), ds);
                if (ret == null){
                    return null;
                }
                return messagePack.read(ret, clazz);
            }
        });
    }

    public Long setnx(final String key, final String value){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.setnx(SafeEncoder.encode(key), SafeEncoder.encode(value));
			}
		});
    }

    public <T> Long setnx(final String key, final T value){
        return this.execute(new RedisCommand<Long>(){
            public Long execute(final BinaryJedis conn) throws Exception {
                byte[] ds = messagePack.write(value);
                return conn.setnx(SafeEncoder.encode(key), ds);
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
			public String execute(final BinaryJedis conn) throws Exception {
				return conn.setex(SafeEncoder.encode(key), seconds, SafeEncoder.encode(value));
			}
		});
    }

    public <T> String setex(final String key, final int seconds, final T value){
        return this.execute(new RedisCommand<String>(){
            public String execute(final BinaryJedis conn) throws Exception {
                byte[] ds = messagePack.write(value);
                return conn.setex(SafeEncoder.encode(key), seconds, ds);
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
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.expire(SafeEncoder.encode(key), timeout);
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
			public Boolean execute(final BinaryJedis conn) throws Exception {
				return conn.exists(SafeEncoder.encode(key));
			}
		});
	}
	/**
	 * 删除Key
	 * @param keys 缓存keys
	 * @return
	 */
	public boolean delete(final String... keys){
		return this.execute(new RedisCommand<Boolean>(){
			public Boolean execute(final BinaryJedis conn) throws Exception {
				return conn.del(SafeEncoder.encodeMany(keys)) > 0;
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
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.incrBy(SafeEncoder.encode(key), amount);
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
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.decrBy(SafeEncoder.encode(key), amount);
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
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.hincrBy(SafeEncoder.encode(key), SafeEncoder.encode(field), amount);
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
			public Long execute(final BinaryJedis conn) throws Exception {
				Pipeline pipe = conn.pipelined();
                byte[] bk = SafeEncoder.encode(key);
				for(String field : nums.keySet()){
					pipe.hincrBy(bk, SafeEncoder.encode(field), nums.get(field));
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
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.hincrBy(SafeEncoder.encode(key), SafeEncoder.encode(field), -1 * amount);
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
			public Long execute(final BinaryJedis conn) throws Exception {
                byte[] bk = SafeEncoder.encode(key);
				Pipeline pipe = conn.pipelined();
				for(String field : nums.keySet()){
					pipe.hincrBy(bk, SafeEncoder.encode(field), -1 * nums.get(field));
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
	public Map<String, Integer> hall(final String key){
		return this.execute(new RedisCommand<Map<String, Integer>>(){
			public Map<String, Integer> execute(final BinaryJedis conn) throws Exception {
                Map<byte[], byte[]> bs = conn.hgetAll(SafeEncoder.encode(key));
                Map<String, Integer> vals = Maps.newHashMap();
                Iterator<byte[]> itor = bs.keySet().iterator();
                while (itor.hasNext()){
                    byte[] k = itor.next();
                    byte[] data = bs.get(k);
                    int v = data == null ? 0 : Integer.parseInt(SafeEncoder.encode(data));
                    vals.put(SafeEncoder.encode(k), v);
                }
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
			public Boolean execute(final BinaryJedis conn) throws Exception {
                byte[] bk = SafeEncoder.encode(key);
				return conn.hdel(bk, SafeEncoder.encodeMany(fields)) > 0;
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
			public Long execute(final BinaryJedis conn) throws Exception {
                byte[] bk = SafeEncoder.encode(key);
				Pipeline pipe = conn.pipelined();
				for(String field : nums.keySet()){
					pipe.hset(bk, SafeEncoder.encode(field), SafeEncoder.encode(String.valueOf(nums.get(field))));
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
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.rpush(SafeEncoder.encode(key), SafeEncoder.encodeMany(values));
			}
		});
	}

    public <T> Long rpush(final Class<T> clazz, final String key, final T... values){
        return this.execute(new RedisCommand<Long>(){
            public Long execute(final BinaryJedis conn) throws Exception {
                byte[][] bytes = new byte[values.length][];
                for (int i = 0; i < values.length; i++) {
                    bytes[i] = messagePack.write(values[i]);
                }
                return conn.rpush(SafeEncoder.encode(key), bytes);
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
			public Long execute(final BinaryJedis conn) throws Exception {
                return conn.lpush(SafeEncoder.encode(key), SafeEncoder.encodeMany(values));
			}
		});
    }

    public <T> Long lpush(final Class<T> clazz, final String key, final T... values){
        return this.execute(new RedisCommand<Long>(){
            public Long execute(final BinaryJedis conn) throws Exception {
                byte[][] bytes = new byte[values.length][];
                for (int i = 0; i < values.length; i++) {
                    bytes[i] = messagePack.write(values[i]);
                }
                return conn.lpush(SafeEncoder.encode(key), bytes);
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
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.llen(SafeEncoder.encode(key));
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
			public List<String> execute(final BinaryJedis conn) throws Exception {
				long start = (page - 1) * limit;
				long end = start + limit;
				List<byte[]> ls = conn.lrange(SafeEncoder.encode(key), start, end);
                List<String> ret = Lists.newArrayList();
                for (byte[] b : ls){
                    if (b != null) {
                        ret.add(SafeEncoder.encode(b));
                    }
                }
                return ret;
			}
		});
    }

    public <T> List<T> lrange(final Class<T> clazz, final String key, final int page, final int limit){
        return this.execute(new RedisCommand<List<T>>(){
            public List<T> execute(final BinaryJedis conn) throws Exception {
                long start = (page - 1) * limit;
                long end = start + limit;
                List<byte[]> ls = conn.lrange(SafeEncoder.encode(key), start, end);
                List<T> ret = Lists.newArrayList();
                for (byte[] b : ls){
                    if (b != null) {
                        ret.add(messagePack.read(b, clazz));
                    }
                }
                return ret;
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
			public String execute(final BinaryJedis conn) throws Exception {
				return conn.ltrim(SafeEncoder.encode(key), start, end);
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
			public String execute(final BinaryJedis conn) throws Exception {
				byte[] bs = conn.lindex(SafeEncoder.encode(key), index);
                if (bs == null){
                    return null;
                }
                return new String(bs);
			}
		});
    }

    public <T> T lindex(final Class<T> clazz, final String key, final int index){
        return this.execute(new RedisCommand<T>(){
            public T execute(final BinaryJedis conn) throws Exception {
                byte[] bs = conn.lindex(SafeEncoder.encode(key), index);
                if (bs == null){
                    return null;
                }
                return messagePack.read(bs, clazz);
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
			public String execute(final BinaryJedis conn) throws Exception {
				return conn.lset(SafeEncoder.encode(key), index, SafeEncoder.encode(value));
			}
		});
    }

    public <T> String lset(final Class<T> clazz, final String key, final int index, final T value){
        return this.execute(new RedisCommand<String>(){
            public String execute(final BinaryJedis conn) throws Exception {
                byte[] bytes = messagePack.write(value);
                return conn.lset(SafeEncoder.encode(key), index, bytes);
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
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.lrem(SafeEncoder.encode(key), 0, SafeEncoder.encode(value));
			}
		});
    }

    public <T> Long lrem(final Class<T> clazz, final String key, final T value){
        return this.execute(new RedisCommand<Long>(){
            public Long execute(final BinaryJedis conn) throws Exception {
                byte[] bytes = messagePack.write(value);
                return conn.lrem(SafeEncoder.encode(key), 0, bytes);
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
			public String execute(final BinaryJedis conn) throws Exception {
				byte[] bs = conn.lpop(SafeEncoder.encode(key));
                if (bs == null){
                    return null;
                }
                return SafeEncoder.encode(bs);
			}
		});
    }
    public <T> T lpop(final Class<T> clazz, final String key){
        return this.execute(new RedisCommand<T>(){
            public T execute(final BinaryJedis conn) throws Exception {
                byte[] bs = conn.lpop(SafeEncoder.encode(key));
                if (bs == null){
                    return null;
                }
                return messagePack.read(bs, clazz);
            }
        });
    }

    public List<String> lpop(final String key, final int limit){
        return this.execute(new RedisCommand<List<String>>(){
            public List<String> execute(final BinaryJedis conn) throws Exception {
                byte[] bk = SafeEncoder.encode(key);
                List<String> resp = Lists.newArrayList();
                for (int i = 0; i < limit; i++) {
                    byte[] bs = conn.lpop(bk);
                    if (bs != null){
                        resp.add(SafeEncoder.encode(bs));
                    }

                }
                return resp;
            }
        });
    }

    public <T> List<T> lpop(final Class<T> clazz, final String key, final int limit){
        return this.execute(new RedisCommand<List<T>>(){
            public List<T> execute(final BinaryJedis conn) throws Exception {
                byte[] bk = SafeEncoder.encode(key);
                List<T> resp = Lists.newArrayList();
                for (int i = 0; i < limit; i++) {
                    byte[] bs = conn.lpop(bk);
                    if (bs != null){
                        resp.add(messagePack.read(bs, clazz));
                    }

                }
                return resp;
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
			public String execute(final BinaryJedis conn) throws Exception {
				byte[] bs = conn.rpop(SafeEncoder.encode(key));
                if (bs == null){
                    return null;
                }
                return SafeEncoder.encode(bs);
			}
		});
    }

    public <T> T rpop(final Class<T> clazz, final String key){
        return this.execute(new RedisCommand<T>(){
            public T execute(final BinaryJedis conn) throws Exception {
                byte[] bs = conn.rpop(SafeEncoder.encode(key));
                if (bs == null){
                    return null;
                }
                return messagePack.read(bs, clazz);
            }
        });
    }

    public List<String> rpop(final String key, final int limit){
        return this.execute(new RedisCommand<List<String>>(){
            public List<String> execute(final BinaryJedis conn) throws Exception {
                byte[] bk = SafeEncoder.encode(key);
                List<String> resp = Lists.newArrayList();
                for (int i = 0; i < limit; i++) {
                    byte[] bs = conn.rpop(bk);
                    if (bs != null){
                        resp.add(SafeEncoder.encode(bs));
                    }

                }
                return resp;
            }
        });
    }

    public <T> List<T> rpop(final Class<T> clazz, final String key, final int limit){
        return this.execute(new RedisCommand<List<T>>(){
            public List<T> execute(final BinaryJedis conn) throws Exception {
                byte[] bk = SafeEncoder.encode(key);
                List<T> resp = Lists.newArrayList();
                for (int i = 0; i < limit; i++) {
                    byte[] bs = conn.rpop(bk);
                    if (bs != null){
                        resp.add(messagePack.read(bs, clazz));
                    }

                }
                return resp;
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
			public List<String> execute(final BinaryJedis conn) throws Exception {
				List<byte[]> lbs = conn.sort(SafeEncoder.encode(key));
                List<String> ret = Lists.newArrayList();
                for (byte[] b : lbs){
                    if (b != null) {
                        ret.add(SafeEncoder.encode(b));
                    }
                }
                return ret;
			}
		});
    }
    
    public Long lpushx(final String key, final String... values){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.lpushx(SafeEncoder.encode(key), SafeEncoder.encodeMany(values));
			}
		});
    }

    public <T> Long lpushx(final Class<T> clazz, final String key, final T... values){
        return this.execute(new RedisCommand<Long>(){
            public Long execute(final BinaryJedis conn) throws Exception {
                byte[][] bytes = new byte[values.length][];
                for (int i = 0; i < values.length; i++) {
                    bytes[i] = messagePack.write(values[i]);
                }
                return conn.lpushx(SafeEncoder.encode(key), bytes);
            }
        });
    }

    public Long rpushx(final String key, final String... values){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.rpushx(SafeEncoder.encode(key), SafeEncoder.encodeMany(values));
			}
		});
    }

    public <T> Long rpushx(final Class<T> clazz, final String key, final T... values){
        return this.execute(new RedisCommand<Long>(){
            public Long execute(final BinaryJedis conn) throws Exception {
                byte[][] bytes = new byte[values.length][];
                for (int i = 0; i < values.length; i++) {
                    bytes[i] = messagePack.write(values[i]);
                }
                return conn.rpushx(SafeEncoder.encode(key), bytes);
            }
        });
    }

    public List<String> blpop(final int timeout, final String... keys){
    	return this.execute(new RedisCommand<List<String>>(){
			public List<String> execute(final BinaryJedis conn) throws Exception {
				List<byte[]> bs = conn.blpop(timeout, SafeEncoder.encodeMany(keys));
                return fromBytes(bs);
			}
		});
    }

    public <T> List<T> blpop(final Class<T> clazz, final int timeout, final String... keys){
        return this.execute(new RedisCommand<List<T>>(){
            public List<T> execute(final BinaryJedis conn) throws Exception {
                List<byte[]> bs = conn.blpop(timeout, SafeEncoder.encodeMany(keys));
                List<T> ret = Lists.newArrayList();
                for (byte[] b : bs){
                    if (b != null) {
                        ret.add(messagePack.read(b, clazz));
                    }
                }
                return ret;
            }
        });
    }

    public List<String> brpop(final int timeout, final String... keys){
    	return this.execute(new RedisCommand<List<String>>(){
			public List<String> execute(final BinaryJedis conn) throws Exception {
				List<byte[]> bs = conn.brpop(timeout, SafeEncoder.encodeMany(keys));
                return fromBytes(bs);
			}
		});
    }

    public <T> List<T> brpop(final Class<T> clazz, final int timeout, final String... keys){
        return this.execute(new RedisCommand<List<T>>(){
            public List<T> execute(final BinaryJedis conn) throws Exception {
                List<byte[]> bs = conn.brpop(timeout, SafeEncoder.encodeMany(keys));
                List<T> ret = Lists.newArrayList();
                for (byte[] b : bs){
                    if (b != null) {
                        ret.add(messagePack.read(b, clazz));
                    }
                }
                return ret;
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
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.sadd(SafeEncoder.encode(key), SafeEncoder.encodeMany(members));
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
			public Long execute(final BinaryJedis conn) throws Exception {
                byte[] bk = SafeEncoder.encode(key);
				Pipeline pipe = conn.pipelined();
				for(Object v : members){
					pipe.sadd(bk, SafeEncoder.encode(String.valueOf(v)));
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
			public Set<String> execute(final BinaryJedis conn) throws Exception {
				Set<byte[]> sk = conn.smembers(SafeEncoder.encode(key));
                Set<String> ret = Sets.newHashSet();
                for (byte[] b : sk){
                    if (b != null) {
                        ret.add(SafeEncoder.encode(b));
                    }
                }
                return ret;
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
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.srem(SafeEncoder.encode(key), SafeEncoder.encodeMany(members));
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
			public String execute(final BinaryJedis conn) throws Exception {
				byte[] bytes = conn.spop(SafeEncoder.encode(key));
                if (bytes == null){
                    return null;
                }
                return SafeEncoder.encode(bytes);
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
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.scard(SafeEncoder.encode(key));
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
			public Boolean execute(final BinaryJedis conn) throws Exception {
				return conn.sismember(SafeEncoder.encode(key), SafeEncoder.encode(member));
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
			public List<String> execute(final BinaryJedis conn) throws Exception {
				List<byte[]> lbs = conn.srandmember(SafeEncoder.encode(key), count);
                if (lbs == null){
                    return Lists.newArrayList();
                }
                return fromBytes(lbs);
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
			public List<String> execute(final BinaryJedis conn) throws Exception {
				List<byte[]> lbs = conn.sort(SafeEncoder.encode(key));
                return fromBytes(lbs);
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
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.zadd(SafeEncoder.encode(key), score, SafeEncoder.encode(member));
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
    public Long zadd(final String key, final Map<String, Double> scoreMembers){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final BinaryJedis conn) throws Exception {
                Map<byte[], Double> ms = Maps.newHashMap();
                Iterator<String> itor = scoreMembers.keySet().iterator();
                while (itor.hasNext()){
                    String k = itor.next();
                    ms.put(SafeEncoder.encode(k), scoreMembers.get(k));
                }
				return conn.zadd(SafeEncoder.encode(key), ms);
			}
		});
    }

    public Set<String> zrange(final String key, final int page, final int limit){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final BinaryJedis conn) throws Exception {
				long start = (page - 1) * limit;
				long end = start + limit;
				Set<byte[]> bs = conn.zrange(SafeEncoder.encode(key), start, end);
                Set<String> ret = Sets.newHashSet();
                for (byte[] b : bs){
                    if (b != null) {
                        ret.add(SafeEncoder.encode(b));
                    }
                }
                return ret;
			}
		});
    }
    public Set<String> zrevrange(final String key, final int page, final int limit){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final BinaryJedis conn) throws Exception {
				long start = (page - 1) * limit;
				long end = start + limit;
				Set<byte[]> bs = conn.zrevrange(SafeEncoder.encode(key), start, end);
                Set<String> ret = Sets.newHashSet();
                for (byte[] b : bs){
                    if (b != null) {
                        ret.add(SafeEncoder.encode(b));
                    }
                }
                return ret;
			}
		});
    }
    
    public Long zrem(final String key, final String... member){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.zrem(SafeEncoder.encode(key), SafeEncoder.encodeMany(member));
			}
		});
    }

    public Double zincrby(final String key, final double score, final String member){
    	return this.execute(new RedisCommand<Double>(){
			public Double execute(final BinaryJedis conn) throws Exception {
				return conn.zincrby(SafeEncoder.encode(key), score, SafeEncoder.encode(member));
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
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.zrank(SafeEncoder.encode(key), SafeEncoder.encode(member));
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
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.zrevrank(SafeEncoder.encode(key), SafeEncoder.encode(member));
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
			public Set<Tuple> execute(final BinaryJedis conn) throws Exception {
				long start = (page - 1) * limit;
				long end = start + limit;
				Set<Tuple> bs = conn.zrangeWithScores(SafeEncoder.encode(key), start, end);
                return bs;
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
			public Set<Tuple> execute(final BinaryJedis conn) throws Exception {
				long start = (page - 1) * limit;
				long end = start + limit;
				return conn.zrevrangeWithScores(SafeEncoder.encode(key), start, end);
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
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.zcard(SafeEncoder.encode(key));
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
			public Double execute(final BinaryJedis conn) throws Exception {
				return conn.zscore(SafeEncoder.encode(key), SafeEncoder.encode(member));
			}
		});
    }
    
    public Long zcount(final String key, final double min, final double max){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.zcount(SafeEncoder.encode(key), min, max);
			}
		});
    }

    public Long zcount(final String key, final String min, final String max){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.zcount(SafeEncoder.encode(key), SafeEncoder.encode(min), SafeEncoder.encode(max));
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
			public Set<String> execute(final BinaryJedis conn) throws Exception {
				Set<byte[]> bs = conn.zrangeByScore(SafeEncoder.encode(key), min, max);
                Set<String> ret = Sets.newHashSet();
                for (byte[] b : bs){
                    if (b != null) {
                        ret.add(new String(b, Protocol.CHARSET));
                    }
                }
                return ret;
			}
		});
    }

    public Set<String> zrangeByScore(final String key, final String min, final String max){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final BinaryJedis conn) throws Exception {
				Set<byte[]> bs = conn.zrangeByScore(SafeEncoder.encode(key), SafeEncoder.encode(min), SafeEncoder.encode(max));
                Set<String> ret = Sets.newHashSet();
                for (byte[] b : bs){
                    if (b != null) {
                        ret.add(new String(b, Protocol.CHARSET));
                    }
                }
                return ret;
			}
		});
    }

    public Set<String> zrevrangeByScore(final String key, final double min, final double max){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final BinaryJedis conn) throws Exception {
				Set<byte[]> bs = conn.zrevrangeByScore(SafeEncoder.encode(key), min, max);
                Set<String> ret = Sets.newHashSet();
                for (byte[] b : bs){
                    if (b != null) {
                        ret.add(new String(b, Protocol.CHARSET));
                    }
                }
                return ret;
			}
		});
    }
    
    public Set<String> zrevrangeByScore(final String key, final String min, final String max){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final BinaryJedis conn) throws Exception {
                Set<byte[]> bs = conn.zrevrangeByScore(SafeEncoder.encode(key), SafeEncoder.encode(min), SafeEncoder.encode(max));
                Set<String> ret = Sets.newHashSet();
                for (byte[] b : bs){
                    if (b != null) {
                        ret.add(new String(b, Protocol.CHARSET));
                    }
                }
                return ret;
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
			public Set<Tuple> execute(final BinaryJedis conn) throws Exception {
				return conn.zrangeByScoreWithScores(SafeEncoder.encode(key), min, max);
			}
		});
    }

    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min){
    	return this.execute(new RedisCommand<Set<Tuple>>(){
			public Set<Tuple> execute(final BinaryJedis conn) throws Exception {
				return conn.zrangeByScoreWithScores(SafeEncoder.encode(key), min, max);
			}
		});
    }

    public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max){
    	return this.execute(new RedisCommand<Set<Tuple>>(){
			public Set<Tuple> execute(final BinaryJedis conn) throws Exception {
				return conn.zrangeByScoreWithScores(SafeEncoder.encode(key), SafeEncoder.encode(min), SafeEncoder.encode(max));
			}
		});
    }
    
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min){
    	return this.execute(new RedisCommand<Set<Tuple>>(){
			public Set<Tuple> execute(final BinaryJedis conn) throws Exception {
				return conn.zrevrangeByScoreWithScores(SafeEncoder.encode(key), SafeEncoder.encode(min), SafeEncoder.encode(max));
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
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.zremrangeByRank(SafeEncoder.encode(key), start, end);
			}
		});
    }

    public Long zremrangeByScore(final String key, final double start, final double end){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.zremrangeByScore(SafeEncoder.encode(key), start, end);
			}
		});
    }
    
    public Long zremrangeByScore(final String key, final String start, final String end){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final BinaryJedis conn) throws Exception {
				return conn.zremrangeByScore(SafeEncoder.encode(key), SafeEncoder.encode(start), SafeEncoder.encode(end));
			}
		});
    }

    @Override
    public <T> boolean put(String key, T value) {
        String ok = this.set(key, value);
        return "ok".equalsIgnoreCase(ok);
    }

    @Override
    public <T> boolean put(String key, T value, int expireSeconds) {
        String ok = this.setex(key, expireSeconds, value);
        return "ok".equalsIgnoreCase(ok);
    }

    @Override
    public boolean remove(final String key) {
        return this.execute(new RedisCommand<Boolean>() {
            @Override
            public Boolean execute(BinaryJedis conn) throws Exception {
                Long ret = conn.del(SafeEncoder.encode(key));
                return ret > 0;
            }
        });
    }

    @Override
    public String gets(String key) {
        return this.get(key);
    }

    @Override
    public List<String> gets(String[] key) {
        return this.mget(key);
    }

    @Override
    public <T> T geto(final Class<T> clazz, String key) {
        return this.get(clazz, key);
    }

    @Override
    public <T> List<T> geto(final Class<T> clazz, String[] key) {
        return this.mget(clazz, key);
    }
}
