package pers.fq.hippo.common;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/29
 */
public class SerialUtilsTest {

    @Test
    public void testObj2Byte() throws Exception {
        String s = "123";
        byte[] b = SerialUtils.obj2Byte(s);
        Assert.assertEquals(SerialUtils.byte2Obj(b, String.class), "123");
    }

    @Test
    public void testObj2ByteWithTimeStampInHead() throws Exception {
        String s = "123";
        byte[] b = SerialUtils.obj2ByteWithTimeStampInHead(s, 111L);
        Assert.assertEquals(SerialUtils.byte2Obj(b, 8, b.length - 8, String.class), "123");
    }
}