package cn.chenforcode.pojo.entity;

import cn.chenforcode.pojo.nodes.CallEdge;
import cn.chenforcode.pojo.nodes.SubCallEdge;
import cn.hutool.core.util.IdUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import soot.SootMethod;

import javax.persistence.Transient;
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
@Node("JvdMethod")
public class JvdMethod {
    @Id
    private String id;
    private String name;
    private String signature;
    private String subSignature;
    private String className;
    private Boolean isSource;
    private Boolean isSink;
    private Boolean isKnown;

    @Transient
    private transient Set<CallEdge> callEdges;
    @Transient
    private transient Set<SubCallEdge> subCallEdges;
    @Transient
    private transient SootMethod sootMethod;
    @Transient
    private transient Set<Integer> taintToRet;
    @Transient
    private transient Map<String, Map<Integer, Integer>> taintToCall;

    public static JvdMethod create(String id, SootMethod method, Set<Integer> taintToRet, Map<String, Map<Integer, Integer>> taintToCall) {
        JvdMethod jvdMethod = new JvdMethod();
        jvdMethod.setId(id);
        jvdMethod.setName(method.getName());
        jvdMethod.setSignature(method.getSignature());
        jvdMethod.setSubSignature(method.getSubSignature());
        jvdMethod.setClassName(method.getDeclaringClass().getName());
        jvdMethod.setSootMethod(method);
        jvdMethod.setTaintToRet(taintToRet);
        jvdMethod.setTaintToCall(taintToCall);
        jvdMethod.setIsSource(false);
        jvdMethod.setIsSink(false);
        jvdMethod.setIsKnown(false);
        return jvdMethod;
    }
}
