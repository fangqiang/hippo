package pers.fq.hippo.common;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/17
 */
public class ByteUtil {

    public static int byte2Int(byte[] a, int offset) {
        return a[offset + 3] & 0xFF |
                (a[offset + 2] & 0xFF) << 8 |
                (a[offset + 1] & 0xFF) << 16 |
                (a[offset] & 0xFF) << 24;
    }

    public static int byte2Int(byte[] a) {
        return byte2Int(a, 0);
    }

    public static byte[] int2Byte(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static byte[] long2Byte(long res) {
        byte[] buffer = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = 64 - (i + 1) * 8;
            buffer[i] = (byte) ((res >> offset) & 0xff);
        }
        return buffer;
    }

    public static long byte2Long(byte[] b, int offset) {
        long values = 0;
        for (int i = 0; i < 8; i++) {
            values <<= 8;
            values |= (b[offset + i] & 0xff);
        }
        return values;
    }

    public static long byte2Long(byte[] b) {
        return byte2Long(b, 0);
    }

    /**
     * 将多个数组合并成一个大数组
     */
    public static byte[] mergeListBytes(List<byte[]> bytes) {
        int size = 0;
        for (byte[] aByte : bytes) {
            size += aByte.length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(size + 4 * bytes.size() + 4);
        buffer.putInt(bytes.size());

        for (byte[] aByte : bytes) {
            buffer.putInt(aByte.length);
            buffer.put(aByte);
        }

        return buffer.array();
    }

    public static List<byte[]> splitListBytes(byte[] bytes) {

        List<byte[]> list = new ArrayList<>();

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int len = buffer.getInt();

        for (int i = 0; i < len; i++) {
            int l = buffer.getInt();
            byte[] b = new byte[l];
            buffer.get(b);
            list.add(b);
        }

        return list;
    }

    public static boolean allZero(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != 0) {
                return false;
            }
        }

        return true;
    }

    public static int compareTo(byte[] buffer1, byte[] buffer2) {
        int offset1 = 0;
        int length1 = buffer1.length;

        int offset2 = 0;
        int length2 = buffer2.length;

        if (buffer1 == buffer2 && offset1 == offset2 && length1 == length2) {
            return 0;
        } else {
            int end1 = offset1 + length1;
            int end2 = offset2 + length2;
            int i = offset1;

            for(int j = offset2; i < end1 && j < end2; ++j) {
                int a = buffer1[i] & 255;
                int b = buffer2[j] & 255;
                if (a != b) {
                    return a - b;
                }

                ++i;
            }

            return length1 - length2;
        }
    }

    public static boolean equalBytes(byte[] a, byte[] b){
        return Arrays.equals(a,b);
    }
}
