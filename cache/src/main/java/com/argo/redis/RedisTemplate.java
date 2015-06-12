package com.argo.redis;

import com.argo.core.utils.IpUtil;
import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Closeable;
import java.io.IOException;

public abstract class RedisTemplate implements BeanNameAware, InitializingBean, DisposableBean, Closeable {

    private String beanName = "";
    protected Logger logger = null;

    private String serverName = null;
    private boolean ALIVE = true;
    private boolean serverDown = false;
    private RedisConfig redisConfig = null;

    private RedisPool jedisPool;
    private JedisPoolConfig config;

    protected MessagePack messagePack = new MessagePack();

    public void afterPropertiesSet() throws Exception {
        logger = LoggerFactory.getLogger(this.getClass() + "." + beanName);
        if (serverName == null) {
            serverName = IpUtil.getHostServerIp()[0];
        }
        redisConfig = new RedisConfig();
        redisConfig.afterPropertiesSet();

        config = new JedisPoolConfig();
        config.setMaxTotal(this.redisConfig.getMaxActive());
        config.setMaxIdle(this.redisConfig.getMaxIdle());
        config.setMaxWaitMillis(this.redisConfig.getTimeoutWait());
        config.setTestOnBorrow(this.redisConfig.getTestOnBorrow());
        config.setTestWhileIdle(this.redisConfig.getTestWhileIdle());

        this.initJedisPool();

        new MonitorThread().start();
    }

    @Override
    public void destroy() throws Exception {
        if (null != this.jedisPool){
            this.jedisPool.destroy();
        }
    }

    @Override
    public void close() throws IOException {
        if (null != this.jedisPool){
            this.jedisPool.close();
        }
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public RedisPool getJedisPool() {
        return jedisPool;
    }

    protected void initJedisPool() {
        this.jedisPool =
            new RedisPool(config, redisConfig.getServiceIp(), redisConfig.getServicePort());
    }

    // 执行具体COMMAND
    public <T> T execute(final RedisCommand<T> action) {
        if (!ALIVE) {
            logger.error("Redis is Still Down.");
            return null;
        }
        BinaryJedis conn = null;
        boolean error = false;
        try {
            conn = getJedisPool().getResource();
            return action.execute(conn);
        } catch (Exception e) {
            serverDown = true;
            error = true;
            logger.error("Execute Redis Command ERROR.", e);
            return null;
        } finally {
            if (conn != null) {
                try {
                    if (error) {
                        this.returnBorkenConnection(conn);
                    } else {
                        this.returnConnection(conn);
                    }
                } catch (Exception e) {
                    logger.error(
                        "Error happen when return jedis to pool, try to close it directly.",
                        e);
                    if (conn.isConnected()) {
                        try {
                            try {
                                conn.quit();
                            } catch (Exception e1) {
                            }
                            conn.disconnect();
                        } catch (Exception e2) {

                        }
                    }
                }
            }
        }
    }

    public String info() {
        return this.execute(new RedisCommand<String>() {
            public String execute(BinaryJedis conn) throws Exception {
                return conn.info();
            }
        });
    }

    /**
     * 关闭数据库连接
     *
     * @param jedis
     */
    private void returnConnection(BinaryJedis jedis) {
        if (null != jedis) {
            try {
                getJedisPool().returnResource(jedis);
            } catch (Exception e) {
                getJedisPool().returnBrokenResource(jedis);
            }
        }
    }

    /**
     * 关闭错误连接
     *
     * @param jedis
     */
    private void returnBorkenConnection(BinaryJedis jedis) {
        if (null != jedis) {
            getJedisPool().returnBrokenResource(jedis);
        }
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    private class MonitorThread extends Thread {
        @Override
        public void run() {
            int sleepTime = 30000;
            int baseSleepTime = 1000;
            while (true) {
                try {
                    // 30秒执行监听
                    int n = sleepTime / baseSleepTime;
                    for (int i = 0; i < n; i++) {
                        if (serverDown) {// 检查到异常，立即进行检测处理
                            break;
                        }
                        Thread.sleep(baseSleepTime);
                    }
                    // 连续做3次连接获取
                    int errorTimes = 0;
                    for (int i = 0; i < 3; i++) {
                        try {
                            BinaryJedis jedis = getJedisPool().getResource();
                            if (jedis == null) {
                                errorTimes++;
                                continue;
                            }
                            returnConnection(jedis);
                            break;
                        } catch (Exception e) {
                            logger.error("Redis链接错误", e);
                            errorTimes++;
                        }
                    }
                    if (errorTimes == 3) {// 3次全部出错，表示服务器出现问题
                        ALIVE = false;
                        serverDown = true; // 只是在异常出现第一次进行跳出处理，后面的按异常检查时间进行延时处理
                        logger.error(beanName + " -> redis[" + getServerName()
                            + "] 服务器连接不上");
                        // 修改休眠时间为5秒，尽快恢复服务
                        sleepTime = 5000;
                    } else {
                        if (ALIVE == false) {
                            ALIVE = true;
                            // 修改休眠时间为30秒，服务恢复
                            sleepTime = 30000;
                            logger.info(beanName + " --> redis["
                                + getServerName() + "] 服务器恢复正常");
                        }
                        serverDown = false;
                        BinaryJedis jedis = getJedisPool().getResource();
                        logger.info(beanName + " --> im redis["
                            + getServerName() + "] 当前记录数：" + jedis.dbSize());
                        returnConnection(jedis);
                    }
                } catch (Exception e) {
                    logger.error("Redis错误", e);
                }
            }
        }
    }
}
