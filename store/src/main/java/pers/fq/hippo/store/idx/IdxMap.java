package pers.fq.hippo.store.idx;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLongArray;


/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/12
 */
public class IdxMap {
    static final long serialVersionUID = 1L;

    public static final byte FREE = 0;
    public static final byte FULL = 1;
    public static final byte REMOVED = 2;

    public final AtomicByteArray states;
    public final AtomicIntegerArray keys;
    protected final AtomicLongArray values;

    protected int no_entry_key;
    protected long no_entry_value;
    protected final int maxSize;

    protected final AtomicInteger used = new AtomicInteger(0);

    public IdxMap(int initialCapacity, int noEntryKey, long noEntryValue) {
        maxSize = initialCapacity;

        initialCapacity = PrimeFinder.nextPrime(initialCapacity * 2);
        this.keys = new AtomicIntegerArray(initialCapacity);
        this.values = new AtomicLongArray(initialCapacity);
        this.states = new AtomicByteArray(initialCapacity);
        this.no_entry_key = noEntryKey;
        this.no_entry_value = noEntryValue;
    }

    protected int insertKey(int val) {
        int hash = val & Integer.MAX_VALUE;
        int index = hash % states.length();
        byte state = states.get(index);

        if (state == FREE) {
            insertKeyAt(index, val);
            return index;       // empty, all done
        }

        if (state == FULL && keys.get(index) == val) {
            return -index - 1;   // already stored
        }

        // already FULL or REMOVED, must probe
        return insertKeyRehash(val, index, hash, state);
    }

    void insertKeyAt(int index, int val) {
        keys.set(index, val);
        states.set(index, FULL);
    }

    int insertKeyRehash(int val, int index, int hash, byte state) {
        // compute the double hash
        final int length = keys.length();
        int probe = 1 + (hash % (length - 2));
        final int loopIndex = index;
        int firstRemoved = -1;

        /**
         * Look until FREE slot or we start to loop
         */
        do {
            // Identify first removed slot
            if (state == REMOVED && firstRemoved == -1) {
                firstRemoved = index;
            }

            index -= probe;
            if (index < 0) {
                index += length;
            }
            state = states.get(index);

            // A FREE slot stops the search
            if (state == FREE) {
                if (firstRemoved != -1) {
                    insertKeyAt(firstRemoved, val);
                    return firstRemoved;
                } else {
                    insertKeyAt(index, val);
                    return index;
                }
            }

            if (state == FULL && keys.get(index) == val) {
                return -index - 1;
            }

            // Detect loop
        } while (index != loopIndex);

        // We inspected all reachable slots and did not find a FREE one
        // If we found a REMOVED slot we return the first one found
        if (firstRemoved != -1) {
            insertKeyAt(firstRemoved, val);
            return firstRemoved;
        }

        // Can a resizing strategy be found that resizes the set?
        throw new IllegalStateException("No free or removed slots available. Key set full?!!");
    }

    public long put(int key, long value) {
        int index = this.insertKey(key);
        return this.doPut(key, value, index);
    }

    private long doPut(int key, long value, int index) {
        long previous = this.no_entry_value;
        if (index < 0) {
            index = -index - 1;
            previous = values.get(index);
        }else{
            used.incrementAndGet();
        }

        values.set(index, value);

        return previous;
    }

    public long get(int key) {
        int index = this.index(key);
        return index < 0 ? this.no_entry_value : values.get(index);
    }

    protected int index(int key) {
        int length = states.length();
        int hash = key & Integer.MAX_VALUE;
        int index = hash % length;
        byte state = states.get(index);
        if (state == 0) {
            return -1;
        } else {
            return state == 1 && keys.get(index) == key ? index : this.indexRehashed(key, index, hash, state);
        }
    }

    int indexRehashed(int key, int index, int hash, byte state) {
        int length = this.keys.length();
        int probe = 1 + hash % (length - 2);
        int loopIndex = index;

        do {
            index -= probe;
            if (index < 0) {
                index += length;
            }

            state = states.get(index);
            if (state == 0) {
                return -1;
            }

            if (key == keys.get(index) && state != 2) {
                return index;
            }
        } while(index != loopIndex);

        return -1;
    }

    public long remove(int key) {
        long prev = this.no_entry_value;
        int index = this.index(key);
        if (index >= 0) {
            prev = values.get(index);
            this.removeAt(index);

            used.decrementAndGet();
        }

        return prev;
    }

    protected void removeAt(int index) {
        values.set(index, no_entry_value);
        keys.set(index, no_entry_key);
        states.set(index, REMOVED);
    }

    public boolean forEachEntry(TIntLongProcedure procedure) {

        int i = keys.length();

        do {
            if (i-- <= 0) {
                return true;
            }
        } while(states.get(i) != 1 || procedure.execute(keys.get(i), values.get(i)));

        return false;
    }

    public int getUsed() {
        return used.get();
    }

    public interface TIntLongProcedure {
        boolean execute(int var1, long var2);

    }

    public static void main(String[] args) {
        IdxMap b = new IdxMap(10,  -1, -1);
        b = new IdxMap(1000_0000,  -1, -1);

        b.put(1,1L);
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