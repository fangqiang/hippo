package pers.fq.hippo.storage.impl;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/11/17
 */
public class CacheImplTest {

    static CacheImpl store;

    @BeforeTest
    public void init(){
        System.out.println(11111);
        store = new CacheImpl(
                Config.CACHE_MAX_SIZE, Config.CACHE_TTL
        );
    }

    @Test
    public void testPut() throws Exception {
        store.put("key", "hello".getBytes());
        Assert.assertEquals(new String(store.get("key")),"hello");
        store.append("key", "world".getBytes(), (a,b)-> "hello_word".getBytes());
        Assert.assertEquals(new String(store.get("key")),"hello_word");
        store.remove("key");
        Assert.assertEquals(store.get("key"),null);
    }
}