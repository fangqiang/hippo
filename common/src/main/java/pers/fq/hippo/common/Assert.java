package pers.fq.hippo.common;

import java.util.Collection;
import java.util.Map;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/23
 */
public class Assert {
    public static void check(boolean expression, String errorMessage){
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static void checkNotEmpty(Collection collection, String errorMessage){
        if (collection == null || collection.size() == 0) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static void checkNotEmpty(Map map, String errorMessage){
        if (map == null || map.size() == 0) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }
}
