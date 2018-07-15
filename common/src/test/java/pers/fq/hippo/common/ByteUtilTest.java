package pers.fq.hippo.common;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/28
 */
public class ByteUtilTest {

    @Test
    public void testByteArrayToInt() throws Exception {
        byte[] bytes = ByteUtil.int2Byte(11);
        byte[] bytess = new byte[5];
        System.arraycopy(bytes, 0, bytess, 1, bytes.length);

        Assert.assertEquals(ByteUtil.byte2Int(bytess, 1), 11);
    }

    @Test
    public void testLong2byte() throws Exception {
        byte[] bytes = ByteUtil.long2Byte(11L);
        byte[] bytess = new byte[9];
        System.arraycopy(bytes, 0, bytess, 1, bytes.length);

        Assert.assertEquals(ByteUtil.byte2Long(bytess, 1), 11);
    }

    @Test
    public void testMergeListBytes() throws Exception {
        byte[] a = new byte[]{101};
        byte[] b = new byte[]{102};

        byte[] c = ByteUtil.mergeListBytes(Arrays.asList(a, b));

        List<byte[]> d = ByteUtil.splitListBytes(c);
    }

    @Test
    public void testSplitListBytes() throws Exception {
        byte[] byte1 = ByteUtil.long2Byte(11L);
        byte[] byte2 = ByteUtil.long2Byte(12L);

        byte[] byte3 = ByteUtil.mergeListBytes(Arrays.asList(byte1, byte2));

        List<byte[]> l = ByteUtil.splitListBytes(byte3);

        Assert.assertEquals(ByteUtil.byte2Long(l.get(0)), 11L);
        Assert.assertEquals(ByteUtil.byte2Long(l.get(1)), 12L);
    }
}