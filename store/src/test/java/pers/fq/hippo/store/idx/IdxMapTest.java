package pers.fq.hippo.store.idx;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/12
 */
public class IdxMapTest {

    @Test
    public void testInsertKey() throws Exception {
        IdxMap map = new IdxMap(10, -1, -1);

        // 第一次put
        Assert.assertEquals(map.put(1, 1), -1);
        // 覆盖put
        Assert.assertEquals(map.put(1, 2), 1);
        Assert.assertEquals(map.getUsed(), 1);
        // put一个hash冲突的key
        Assert.assertEquals(map.put(24, 4), -1);
        Assert.assertEquals(map.getUsed(), 2);

        // 重新使用一个删除后的key
        Assert.assertEquals(map.remove(24), 4);
        Assert.assertEquals(map.getUsed(), 1);
        Assert.assertEquals(map.put(24, 5), -1);
        Assert.assertEquals(map.getUsed(), 2);
    }

    @Test
    public void testRemoveKey() throws Exception {
        IdxMap map = new IdxMap(10, -1, -1);

        // 删除不存在的key
        Assert.assertEquals(map.remove(1), -1);

        // 删除第一个找到的key
        Assert.assertEquals(map.put(1, 2), -1);
        Assert.assertEquals(map.getUsed(), 1);
        Assert.assertEquals(map.remove(1), 2);
        Assert.assertEquals(map.getUsed(), 0);

        // 删除第二个找到的key
        Assert.assertEquals(map.put(1, 2), -1);
        Assert.assertEquals(map.put(24, 4), -1);
        Assert.assertEquals(map.getUsed(), 2);
        Assert.assertEquals(map.remove(24), 4);
        Assert.assertEquals(map.getUsed(), 1);

        // 删除已经删除的key
        Assert.assertEquals(map.remove(24), -1);
        Assert.assertEquals(map.getUsed(), 1);

        // 删除第三次hash后，仍然找不到的key
        Assert.assertEquals(map.remove(47), -1);
        Assert.assertEquals(map.getUsed(), 1);
    }

    @Test
    public void testGetKey() throws Exception {
        IdxMap map = new IdxMap(10, -1, -1);

        // get一个空值
        Assert.assertEquals(map.get(1), -1);
        Assert.assertEquals(map.getUsed(), 0);

        // get一个值
        Assert.assertEquals(map.put(1, 2), -1);
        Assert.assertEquals(map.get(1), 2);
        Assert.assertEquals(map.getUsed(), 1);


        // get一个值(有哈希冲突)
        Assert.assertEquals(map.put(24, 4), -1);
        Assert.assertEquals(map.get(24), 4);
        Assert.assertEquals(map.getUsed(), 2);

        // get一个删除的值(有哈希冲突)
        Assert.assertEquals(map.remove(24), 4);
        Assert.assertEquals(map.getUsed(), 1);
        Assert.assertEquals(map.get(24), -1);

        // get一个空值(有哈希冲突)
        Assert.assertEquals(map.get(47), -1);
        Assert.assertEquals(map.getUsed(), 1);
    }
}