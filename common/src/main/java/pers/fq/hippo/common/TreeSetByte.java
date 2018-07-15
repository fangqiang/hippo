package pers.fq.hippo.common;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/26
 */
public class TreeSetByte {

    TreeSet<byte[]> set = new TreeSet(new Comparator<byte[]>(){
        @Override
        public int compare(byte[] o1, byte[] o2) {
            return ByteUtil.compareTo(o1, o2);
        }
    });

    public TreeSetByte(){

    }

    public TreeSetByte(Collection<byte[]> collection){
        set.addAll(collection);
    }

    public void add(byte[] o){
        set.add(o);
    }

    public int size(){
        return set.size();
    }

    public boolean contains(byte [] o){
        return set.contains(o);
    }

    public void remove(byte [] o){
        set.remove(o);
    }
}
