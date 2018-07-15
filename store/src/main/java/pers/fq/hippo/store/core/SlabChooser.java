//package pers.fq.hippo.store.core;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.ArrayList;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/18
// */
//public class SlabChooser {
//
//    private static final Logger logger = LoggerFactory.getLogger(SlabManager.class);
//
//    final ArrayList<Slab> slabs = new ArrayList();
//
//    public SlabChooser() {
//        int id = 0;
//        for (int i = Config.MIN_CHUNK_SIZE; i <= Config.MAX_CHUNK_SIZE; i <<= 1) {
//            slabs.add(new Slab(id++, i));
//            logger.info("init slab chunk_size: {}", i);
//        }
//    }
//
//    public Slab getSlabByIdx(int idx) {
//        return slabs.get(idx);
//    }
//
//    public Slab getSlabBySize(int size) {
//
//        for (int i = 0; i < slabs.size(); i++) {
//            Slab slab = slabs.get(i);
//            if (size <= slab.chunkSize) {
//                return slab;
//            }
//        }
//
//        throw new RuntimeException("size over flow, " + size);
//    }
//}
