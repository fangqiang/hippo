//package pers.fq.hippo.store.core;
//
//import pers.fq.hippo.common.ByteUtil;
//
//public class IdxValue {
//    public final int slot;
//    public final int offset;
//    public final long lastUpdateTime;
//    public final byte[] idx;
//
//    private IdxValue(int slot, int offset, long lastUpdateTime) {
//        this.slot = slot;
//        this.offset = offset;
//        this.lastUpdateTime = lastUpdateTime;
//
//        idx = new byte[13];
//        update(slot, offset);
//
//        // 把最后修改时间写进索引
//        System.arraycopy(ByteUtil.long2Byte(lastUpdateTime), 0, idx, 5, 8);
//    }
//
//    private IdxValue(byte[] idx) {
//        this.idx = idx;
//        this.slot = (int) idx[0];
//        this.offset = ByteUtil.byte2Int(idx, 1);
//        this.lastUpdateTime = ByteUtil.byte2Long(idx, 5);
//    }
//
//    public void update(final int slot, final int offset) {
//        idx[0] = (byte) slot;
//        System.arraycopy(ByteUtil.int2Byte(offset), 0, idx, 1, 4);
//    }
//
//    static IdxValue parseIdx(byte[] v) {
//        return new IdxValue(v);
//    }
//
//    static IdxValue newIdx(int slot, int offset) {
//        return new IdxValue(slot, offset, System.currentTimeMillis());
//    }
//}