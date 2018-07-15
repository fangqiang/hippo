package pers.fq.hippo.common;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/12/15
 */
public class LZ4Utils {
    public static final LZ4Factory factory = LZ4Factory.fastestInstance();

    public static byte[] compress(byte[] data) {
        LZ4Compressor compressor = factory.fastCompressor();
        return compressor.compress(data);
    }

    public static byte[] decompress(byte[] compressed, int decompressedLength) {
        LZ4FastDecompressor decompressor = factory.fastDecompressor();
        return decompressor.decompress(compressed, decompressedLength);
    }
}
