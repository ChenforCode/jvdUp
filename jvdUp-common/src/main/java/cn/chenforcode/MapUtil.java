package cn.chenforcode;


import soot.SootField;
import soot.Value;
import soot.jimple.internal.JInstanceFieldRef;

import java.util.Map;
import java.util.Set;

/**
 * @author yumu
 * @date 2023/2/7 22:20
 * @description
 */
public class MapUtil {
    public static void addOrInitByTaintMap(Map<Value, Set<Integer>> map, Value key, Set<Integer> values) {
        if (values == null) {
            return;
        }
        if (map.containsKey(key)) {
            map.get(key).addAll(values);
        } else {
            map.put(key, values);
        }
    }

    public static void addOrInitByFieldMap(Map<SootField, Set<Integer>> map, Value leftOp, Set<Integer> values) {
        if (values == null) {
            return;
        }
        SootField key = ((JInstanceFieldRef) leftOp).getField();
        if (map.containsKey(key)) {
            map.get(key).addAll(values);
        } else {
            map.put(key, values);
        }
    }
}
