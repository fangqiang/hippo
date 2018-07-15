package pers.fq.hippo.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.fq.hippo.common.Assert;
import pers.fq.hippo.storage.Slab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/18
 */
final public class SlabManager {

    private static final Logger logger = LoggerFactory.getLogger(SlabManager.class);

    /**
     * 这个地方非线程安全，但考虑到不可变结构，所以问题不大
     */
    private final ArrayList<SlabImpl> slabs = new ArrayList();

    /**
     * 1, 2, 3, 4, 5, 8, 12, 16, 20, 24, 32, 40, 48, 64, 80, 96, 128, 160, 192, 256, 320, 384, 512, 640, 768, 1024
     */
    private static final List<Integer> CHUNK_SIZE = Arrays.asList(1, 4, 8, 16, 32, 64, 96, 160, 256, 384, 512, 640, 768, 1024);

    private final static SlabManager singleton = new SlabManager();

    public static synchronized SlabManager instance(){
        return singleton;
    }

    private SlabManager() {
        int id = 0;
        for (Integer chunkSize : CHUNK_SIZE) {
            Assert.check(Config.PAGE_SIZE % chunkSize == 0, "chunkSize is not valid " + chunkSize);
            slabs.add(new SlabImpl(id++, chunkSize * 1024));
            logger.info("init slab chunk_size: {}", chunkSize * 1024);
        }
    }

    public Slab getSlabBySize(int size) {
        for (int i = 0; i < slabs.size(); i++) {
            Slab slab = slabs.get(i);
            if (size <= slab.chunkSize) {
                return slab;
            }
        }

        throw new RuntimeException("size over flow, " + size);
    }

    public Slab getSlab(int slabId) {
        return slabs.get(slabId);
    }
}