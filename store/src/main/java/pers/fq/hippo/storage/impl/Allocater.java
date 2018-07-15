package pers.fq.hippo.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.fq.hippo.store.tag.Nullable;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/18
 */
public class Allocater {
    private static final Logger logger = LoggerFactory.getLogger(Allocater.class);

    public static final AtomicLong usedSize = new AtomicLong(0L);

    public static boolean isFull = false;

    @Nullable
    public synchronized static Page allocatePage() {
        if (usedSize.get() >= Config.MAX_OFF_HEAP_SIZE) {
            isFull = true;
            return null;
        }

        try {
            ByteBuffer buffer = ByteBuffer.allocateDirect((int) Config.PAGE_SIZE);
            usedSize.addAndGet(Config.PAGE_SIZE);
            return new Page(buffer);
        } catch (OutOfMemoryError e) {
            isFull = true;
            return null;
        } catch (Throwable e) {
            logger.error("", e);
            return null;
        }
    }
}
