package pers.fq.hippo.common;

import java.util.*;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/22
 */
public class App {
    public static void main(String[] args) {
        String s = new String("123");
        String s1 = "1" + new String("23");

        List<byte[]> l1 = new ArrayList<>();

        l1.add("012".getBytes());
        l1.add("234".getBytes());
        l1.add("123".getBytes());

        TreeSetByte treeSetByte = new TreeSetByte(l1);

        System.out.println(treeSetByte.contains("123".getBytes()));
        treeSetByte.remove("123".getBytes());
    }
}
