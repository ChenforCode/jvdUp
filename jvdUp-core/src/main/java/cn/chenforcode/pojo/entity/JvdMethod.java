package cn.chenforcode.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import soot.SootMethod;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yumu
 * @date 2023/2/7 16:21
 * @description 带有污点分析结果的method
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JvdMethod {
    private String name;
    private SootMethod sootMethod;
    private Set<Integer> taintToRet;
    private Map<String, List<Integer>> taintToCall;

    public static JvdMethod create(SootMethod method, Set<Integer> taintToRet, Map<String, List<Integer>> taintToCall) {
        JvdMethod jvdMethod = new JvdMethod();
        jvdMethod.setName(method.getName());
        jvdMethod.setSootMethod(method);
        jvdMethod.setTaintToRet(taintToRet);
        jvdMethod.setTaintToCall(taintToCall);
        return jvdMethod;
    }
}
