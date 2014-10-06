package com.argo.redis;

import com.argo.core.ContextConfig;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.List;

public class RedisBuketTest {

    private static RedisBuket redisBuket;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.setProperty(ContextConfig.RUNNING_ENV, "dev");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-redis.xml");
        redisBuket = context.getBean("redisBuket", RedisBuket.class);
    }

    @Test
    public void testMget() throws Exception {
        List<String> ret = redisBuket.mget("key1", "key2");
        Assert.assertEquals(0, ret.size());
    }

    @Test
    public void testMget1() throws Exception {
        List<Person> ret = redisBuket.mget(Person.class, "key1", "key2");
        Assert.assertEquals(0, ret.size());
    }

    @Test
    public void testGet() throws Exception {
       String ret = redisBuket.get("key1");
       Assert.assertNull(ret);
    }

    @Test
    public void testGet1() throws Exception {
        Person ret = redisBuket.get(Person.class, "key1");
        Assert.assertNull(ret);
    }

    @Test
    public void testGetSet() throws Exception {
        String ret = redisBuket.getSet("key1", "key1");
        Assert.assertNull(ret);
    }

    @Test
    public void testGetSet1() throws Exception {
        Person p = new Person();
        p.id = 1L;
        p.name = "test";
        p.createAt = new Date();

        Person ret = redisBuket.getSet(Person.class, "key3", p);
        Assert.assertNull(ret);

        ret = redisBuket.get(Person.class, "key3");
        Assert.assertNotNull(ret);
    }

    @Test
    public void testSetnx() throws Exception {

    }

    @Test
    public void testSetnx1() throws Exception {

    }

    @Test
    public void testSetex() throws Exception {

    }

    @Test
    public void testSetex1() throws Exception {

    }

    @Test
    public void testExpire() throws Exception {

    }

    @Test
    public void testExists() throws Exception {

    }

    @Test
    public void testDelete() throws Exception {

    }

    @Test
    public void testIncr() throws Exception {

    }

    @Test
    public void testDecr() throws Exception {

    }

    @Test
    public void testHincr() throws Exception {

    }

    @Test
    public void testHincr1() throws Exception {

    }

    @Test
    public void testHdecr() throws Exception {

    }

    @Test
    public void testHdecr1() throws Exception {

    }

    @Test
    public void testHall() throws Exception {

    }

    @Test
    public void testHrem() throws Exception {

    }

    @Test
    public void testHset() throws Exception {

    }

    @Test
    public void testRpush() throws Exception {

    }

    @Test
    public void testRpush1() throws Exception {

    }

    @Test
    public void testLpush() throws Exception {

    }

    @Test
    public void testLpush1() throws Exception {

    }

    @Test
    public void testLlen() throws Exception {

    }

    @Test
    public void testLrange() throws Exception {

    }

    @Test
    public void testLrange1() throws Exception {

    }

    @Test
    public void testLtrim() throws Exception {

    }

    @Test
    public void testLindex() throws Exception {

    }

    @Test
    public void testLindex1() throws Exception {

    }

    @Test
    public void testLset() throws Exception {

    }

    @Test
    public void testLset1() throws Exception {

    }

    @Test
    public void testLrem() throws Exception {

    }

    @Test
    public void testLrem1() throws Exception {

    }

    @Test
    public void testLpop() throws Exception {

    }

    @Test
    public void testLpop1() throws Exception {

    }

    @Test
    public void testLpop2() throws Exception {

    }

    @Test
    public void testLpop3() throws Exception {

    }

    @Test
    public void testRpop() throws Exception {

    }

    @Test
    public void testRpop1() throws Exception {

    }

    @Test
    public void testRpop2() throws Exception {

    }

    @Test
    public void testRpop3() throws Exception {

    }

    @Test
    public void testLsort() throws Exception {

    }

    @Test
    public void testLpushx() throws Exception {

    }

    @Test
    public void testLpushx1() throws Exception {

    }

    @Test
    public void testRpushx() throws Exception {

    }

    @Test
    public void testRpushx1() throws Exception {

    }

    @Test
    public void testBlpop() throws Exception {

    }

    @Test
    public void testBlpop1() throws Exception {

    }

    @Test
    public void testBrpop() throws Exception {

    }

    @Test
    public void testBrpop1() throws Exception {

    }

    @Test
    public void testSadd() throws Exception {

    }

    @Test
    public void testSadd1() throws Exception {

    }

    @Test
    public void testSmembers() throws Exception {

    }

    @Test
    public void testSrem() throws Exception {

    }

    @Test
    public void testSpop() throws Exception {

    }

    @Test
    public void testScard() throws Exception {

    }

    @Test
    public void testSismember() throws Exception {

    }

    @Test
    public void testSrandmember() throws Exception {

    }

    @Test
    public void testSsort() throws Exception {

    }

    @Test
    public void testZadd() throws Exception {

    }

    @Test
    public void testZadd1() throws Exception {

    }

    @Test
    public void testZrange() throws Exception {

    }

    @Test
    public void testZrevrange() throws Exception {

    }

    @Test
    public void testZrem() throws Exception {

    }

    @Test
    public void testZincrby() throws Exception {

    }

    @Test
    public void testZrank() throws Exception {

    }

    @Test
    public void testZrevrank() throws Exception {

    }

    @Test
    public void testZrangeWithScores() throws Exception {

    }

    @Test
    public void testZrevrangeWithScores() throws Exception {

    }

    @Test
    public void testZcard() throws Exception {

    }

    @Test
    public void testZscore() throws Exception {

    }

    @Test
    public void testZcount() throws Exception {

    }

    @Test
    public void testZcount1() throws Exception {

    }

    @Test
    public void testZrangeByScore() throws Exception {

    }

    @Test
    public void testZrangeByScore1() throws Exception {

    }

    @Test
    public void testZrevrangeByScore() throws Exception {

    }

    @Test
    public void testZrevrangeByScore1() throws Exception {

    }

    @Test
    public void testZrangeByScoreWithScores() throws Exception {

    }

    @Test
    public void testZrevrangeByScoreWithScores() throws Exception {

    }

    @Test
    public void testZrangeByScoreWithScores1() throws Exception {

    }

    @Test
    public void testZrevrangeByScoreWithScores1() throws Exception {

    }

    @Test
    public void testZremrangeByRank() throws Exception {

    }

    @Test
    public void testZremrangeByScore() throws Exception {

    }

    @Test
    public void testZremrangeByScore1() throws Exception {

    }
}