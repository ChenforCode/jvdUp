package cn.chenforcode;


import soot.Value;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yumu
 * @date 2023/2/7 22:20
 * @description
 */
public class MapUtil {
    public static void addOrInitByTaintMap(Map<Value, Set<Integer>> map, Value key, Set<Integer> values) {
        if (map.containsKey(key)) {
            map.get(key).addAll(values);
        } else {
            map.put(key, values);
        }
    }
}
