package pers.fq.hippo.common;

import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/12/3
 */
public class ConsistHashWithVirtualNode {

    static int virtualNode = 100;

    public static TreeMap<Integer,String> hashWithVirtualNode(Set<String> ips){
        TreeMap<Integer, String> hashIp = new TreeMap<>();

        // 每个ip配置100个虚拟节点
        for (String ip : ips) {
            for (int i = 0; i < virtualNode; i++) {
                String vip = ip + "#" + i;
                int hash = hash(vip);
                hashIp.put(hash, ip);
            }
        }
        return hashIp;
    }

    public static <T> T get(TreeMap<Integer,T> map, String key){
        return get(map, hash(key));
    }

    public static <T> T get(TreeMap<Integer,T> map, int hash){
        Map.Entry<Integer, T> entry = map.floorEntry(hash);
        return entry != null? entry.getValue() : map.lastEntry().getValue();
    }

    public static int hash(String key){
        return Hashing.md5().hashString(key, Charset.defaultCharset()).asInt();
    }
}
