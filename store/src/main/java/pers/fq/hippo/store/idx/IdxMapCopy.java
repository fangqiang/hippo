package pers.fq.hippo.store.idx;

import gnu.trove.impl.HashFunctions;
import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.procedure.TIntLongProcedure;


/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/12
 */
public class IdxMapCopy {
    static final long serialVersionUID = 1L;

    protected final long[] _values;
    public final int[] _set;
    protected int no_entry_key;
    protected long no_entry_value;
    public final byte[] _states;

    public IdxMapCopy(int initialCapacity, int noEntryKey, long noEntryValue) {
        this._set = new int[initialCapacity];
        this._values = new long[initialCapacity];
        this._states = new byte[initialCapacity];
        this.no_entry_key = noEntryKey;
        this.no_entry_value = noEntryValue;
    }

    protected int insertKey(int val) {
        int hash = HashFunctions.hash(val) & 2147483647;
        int index = hash % this._states.length; // length is fixed
//        byte state = this.states[index];
        byte state = SyncByte.getByte(_states, index);
        if (state == 0) {
            this.insertKeyAt(index, val);
            return index;
        } else {
//            return state == 1 && this.keys[index] == val ? -index - 1 : this.insertKeyRehash(val, index, hash, state);
            return state == 1 && SyncInt.getInt(_set, index) == val ? -index - 1 : this.insertKeyRehash(val, index, hash, state);
        }
    }

    void insertKeyAt(int index, int val) {
//        this.keys[index] = val;
        SyncInt.setInt(_set, index, val);
//        this.states[index] = 1;
        SyncByte.setByte(_states, index, (byte) 1);
    }

    int insertKeyRehash(int val, int index, int hash, byte state) {
        int length = this._set.length;
        int probe = 1 + hash % (length - 2);
        int loopIndex = index;
        int firstRemoved = -1;

        do {
            if (state == 2 && firstRemoved == -1) {
                firstRemoved = index;
            }

            index -= probe;
            if (index < 0) {
                index += length;
            }

//            state = this.states[index];
            state = SyncByte.getByte(_states, index);
            if (state == 0) {
                if (firstRemoved != -1) {
                    this.insertKeyAt(firstRemoved, val);
                    return firstRemoved;
                }

                this.insertKeyAt(index, val);
                return index;
            }

//            if (state == 1 && this.keys[index] == val) {
            if (state == 1 && SyncInt.getInt(_set, index) == val) {
                return -index - 1;
            }
        } while(index != loopIndex);

        if (firstRemoved != -1) {
            this.insertKeyAt(firstRemoved, val);
            return firstRemoved;
        } else {
            throw new IllegalStateException("No free or removed slots available. Key set full?!!");
        }
    }

    public long put(int key, long value) {
        int index = this.insertKey(key);
        return this.doPut(key, value, index);
    }

    private long doPut(int key, long value, int index) {
        long previous = this.no_entry_value;
        if (index < 0) {
            index = -index - 1;
//            previous = this.values[index];
            previous = SyncLong.getLong(_values, index);
        }

//        this.values[index] = value;
        SyncLong.setLong(_values, index, value);

        return previous;
    }

    public long get(int key) {
        int index = this.index(key);
//        return index < 0 ? this.no_entry_value : this.values[index];
        return index < 0 ? this.no_entry_value : SyncLong.getLong(_values, index);
    }

    protected int index(int key) {
        byte[] states = this._states;
        int[] set = this._set;
        int length = states.length;
        int hash = HashFunctions.hash(key) & 2147483647;
        int index = hash % length;
//        byte state = states[index];
        byte state = SyncByte.getByte(states, index);
        if (state == 0) {
            return -1;
        } else {
//            return state == 1 && set[index] == key ? index : this.indexRehashed(key, index, hash, state);
            return state == 1 && SyncInt.getInt(set, index) == key ? index : this.indexRehashed(key, index, hash, state);
        }
    }

    int indexRehashed(int key, int index, int hash, byte state) {
        int length = this._set.length;
        int probe = 1 + hash % (length - 2);
        int loopIndex = index;

        do {
            index -= probe;
            if (index < 0) {
                index += length;
            }

//            state = this.states[index];
            state = SyncByte.getByte(_states, index);
            if (state == 0) {
                return -1;
            }

//            if (key == this.keys[index] && state != 2) {
            if (key == SyncInt.getInt(_set, index) && state != 2) {
                return index;
            }
        } while(index != loopIndex);

        return -1;
    }

    public long remove(int key) {
        long prev = this.no_entry_value;
        int index = this.index(key);
        if (index >= 0) {
//            prev = this.values[index];
            prev = SyncLong.getLong(_values, index);
            this.removeAt(index);
        }

        return prev;
    }

    protected void removeAt(int index) {
//        this.values[index] = this.no_entry_value;
//        super.removeAt(index);

        SyncLong.setLong(_values, index, no_entry_value);
        SyncInt.setInt(_set, index, this.no_entry_key);
        SyncByte.setByte(_states, index, (byte) 2);
    }

    public boolean forEachEntry(TIntLongProcedure procedure) {
        byte[] states = this._states;
        int[] keys = this._set;
        long[] values = this._values;
        int i = keys.length;

        do {
            if (i-- <= 0) {
                return true;
            }
//        } while(states[i] != 1 || procedure.execute(keys[i], values[i]));
        } while(SyncByte.getByte(states, i) != 1 || procedure.execute(SyncInt.getInt(keys, i), SyncLong.getLong(values ,i)));

        return false;
    }

    public static void main(String[] args) {
        TIntLongHashMap b = new TIntLongHashMap(10, 0.5f,  -1, -1);

        b.put(1,1L);
        b.put(24,3L);
        System.out.println(b.get(24));

        for (int i = 2; i < 15; i++) {
            b.put(i,i);
        }

        System.out.println(b.get(24));


        b.put(1,2L);
        b.put(24,3L);
        b.remove(24);
        b.put(24,4);
        System.out.println(b.get(1));
//        b.put(24,24L);
        b.remove(1);

        System.out.println(b.get(1));
        b.put(1,1L);
        System.out.println(b.get(1));

        b.put(2,1L);
        b.put(2,2L);
        b.put(4,1L);
        b.put(3,1L);
        b.forEachEntry((k,v)->{
            System.out.println(k +" " +v);
            return true;
        });
    }
}