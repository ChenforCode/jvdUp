package cn.chenforcode.analysis;


import cn.chenforcode.MapUtil;
import cn.chenforcode.container.DataContainer;
import cn.chenforcode.pojo.entity.JvdMethod;
import cn.chenforcode.pojo.nodes.CallEdge;
import cn.chenforcode.pojo.nodes.SubCallEdge;
import org.springframework.util.CollectionUtils;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.util.Chain;

import java.util.*;

import static cn.chenforcode.container.DataContainer.methodTaint;

/**
 * @author yumu
 * @date 2023/2/3 20:20
 * @description
 */
public class TaintAnalysis extends ForwardFlowAnalysis<Unit, Map<Value, TaintInfo>> {

    private SootMethod method;
    private boolean isInit = false;
    private AnalysisContext context;
    private Map<Value, Set<Integer>> taintMap;
    private Map<String, List<Integer>> interTaint;
    private Value retVal;

    public TaintAnalysis(SootMethod method, DirectedGraph<Unit> graph, AnalysisContext context) {
        super(graph);
        this.method = method;
        this.context = context;
        this.taintMap = new HashMap<>();
        this.interTaint = new HashMap<>();
        System.out.println(graph);
    }

    @Override
    protected void flowThrough(Map<Value, TaintInfo> in, Unit unit, Map<Value, TaintInfo> out) {
        if (!isInit) {
            List<Local> methodArgs = method.getActiveBody().getParameterLocals();
            if (!CollectionUtils.isEmpty(methodArgs)) {
                for (Local arg : methodArgs) {
                    taintMap.put(arg, new HashSet<>());
                }
            }
            Chain<SootField> fields = method.getDeclaringClass().getFields();
            if (!CollectionUtils.isEmpty(fields)) {
                for (SootField field : fields) {
                    //成员变量都属于this参数，即0参
//                    taintMap.put(new JInstanceFieldRef(, field), Collections.singletonList(0));
                }
            }
            //初始化完毕
            isInit = true;
        }
        Stmt stmt = (Stmt) unit;
        System.out.println("Cur analysis: " + stmt);
        if (stmt instanceof JInvokeStmt) {
            processInvoke(((JInvokeStmt) stmt).getInvokeExpr());
        } else if (stmt instanceof AssignStmt) {
            //赋值语句
            AssignStmt assignStmt = (AssignStmt) stmt;
            Value leftOp = assignStmt.getLeftOp();
            Value rightOp = assignStmt.getRightOp();
            if (rightOp instanceof InvokeExpr) {
                //赋值型函数调用
                InvokeExpr invokeExpr = (InvokeExpr) rightOp;
                processInvoke(invokeExpr);
                String name = invokeExpr.getMethod().getName();
                JvdMethod jvdMethod = methodTaint.get(name);
                Set<Integer> taintToRet = jvdMethod.getTaintToRet();
                if (!CollectionUtils.isEmpty(taintToRet)) {
                    //index指，target中的第几个函数会影响
                    for (Integer index : taintToRet) {
                        //这个代表了target中的函数
                        Value arg = invokeExpr.getArg(index - 1);
                        MapUtil.addOrInitByTaintMap(taintMap, leftOp, taintMap.getOrDefault(arg, Collections.emptySet()));
                    }

                }
            } else if (rightOp instanceof JimpleLocal) {
                //普通的右值赋值，如果右值在污点里
                if (taintMap.containsKey(rightOp)) {
                    MapUtil.addOrInitByTaintMap(taintMap, leftOp, taintMap.get(rightOp));
                }
            } else if (rightOp instanceof StaticFieldRef) {
                // 静态变量的右侧赋值

            }
        } else if (stmt instanceof JIdentityStmt) {
            // 参数 -> 变量的绑定语句
            JIdentityStmt jIdentityStmt = (JIdentityStmt) stmt;
            Value leftOp = jIdentityStmt.getLeftOp();
            Value rightOp = jIdentityStmt.getRightOp();
            Set<Integer> set = new HashSet<>();
            if (rightOp instanceof ThisRef) {
                set.add(0);
            } else if (rightOp instanceof ParameterRef) {
                set.add(((ParameterRef) rightOp).getIndex() + 1);
            }
            MapUtil.addOrInitByTaintMap(taintMap, leftOp, set);

        } else if (stmt instanceof ReturnStmt) {
            // return 语句
            ReturnStmt returnStmt = (ReturnStmt) stmt;
            this.retVal = returnStmt.getOp();
        }
    }

    @Override
    protected Map<Value, TaintInfo> newInitialFlow() {
        return new HashMap<>();
    }

    @Override
    protected void merge(Map<Value, TaintInfo> in1, Map<Value, TaintInfo> in2, Map<Value, TaintInfo> out) {
        out = new HashMap<>();
        out.putAll(in1);
        out.putAll(in2);
    }

    @Override
    protected void copy(Map<Value, TaintInfo> source, Map<Value, TaintInfo> target) {
        target.clear();
        target.putAll(source);
    }

    public void analysis() {
        super.doAnalysis();
        //分析完毕之后，保存这个函数的分析结果
        Set<Integer> ret = new HashSet<>();
        if (this.retVal != null) {
            if (taintMap.containsKey(this.retVal)) {
                //TODO 校验是否会被field污染
                ret.addAll(taintMap.get(this.retVal));
            }
        }

        methodTaint.put(method.getName(), JvdMethod.create(method, ret, interTaint));
    }

    //递归函数
    private void processInvoke(InvokeExpr invokeExpr) {
        SootMethod target = invokeExpr.getMethod();
        if (methodTaint.containsKey(target.getName())) {
            CallEdge callEdge = new CallEdge(method, target, new ArrayList<>());
            callEdge.setInterTaintInfo(new ArrayList<>());
            DataContainer.callEdges.add(callEdge);
            return;
        }
        if (context.getDepth() >= 5) {
            //如果调用图过深，或者当前调用没有参数关联，就不再往下分析
            return;
        }
        if (CollectionUtils.isEmpty(method.getActiveBody().getParameterLocals())) {
            return;
        }
        //开始构建此次调用的一个call边关系
        //获取本次调用的所有参数，来
        List<Value> args = invokeExpr.getArgs();
        List<Integer> callTaint = new ArrayList<>();
        callTaint.add(-1);
        for (Value arg : args) {
            if (taintMap.containsKey(arg)) {
                callTaint.add(new ArrayList<>(taintMap.get(arg)).get(0));
            } else {
                callTaint.add(-1);
            }
        }
        interTaint.put(target.getName(), callTaint);
        CallEdge callEdge = new CallEdge(method, target, callTaint);
        DataContainer.callEdges.add(callEdge);
        new MethodAnalysis().analysisMethod(target, new AnalysisContext(target, context.getDepth() + 1));
        //分析子类
        Set<SootClass> subClasses = DataContainer.classToSubClass.get(target);
        if (!CollectionUtils.isEmpty(subClasses)) {
            for (SootClass subClass : subClasses) {
                //重新获取子类中的方法进行分析
                SootMethod subMethod = subClass.getMethod(target.getSubSignature());
                List<Value> subArgs = invokeExpr.getArgs();
                List<Integer> subCallTaint = new ArrayList<>();
                callTaint.add(-1);
                for (Value arg : args) {
                    if (taintMap.containsKey(arg)) {
                        callTaint.add(new ArrayList<>(taintMap.get(arg)).get(0));
                    } else {
                        callTaint.add(-1);
                    }
                }
                interTaint.put(target.getName(), callTaint);
                new MethodAnalysis().analysisMethod(subMethod, new AnalysisContext(subMethod, context.getDepth() + 1));
                SubCallEdge subCallEdge = new SubCallEdge(method, subMethod, subCallTaint);
                DataContainer.subCallEdges.add(subCallEdge);
            }
        }
    }

    public String func(int arg) {
        int var1 = arg;
        List<Integer> list = new ArrayList<>();
        list.add(var1);
        return list.toString();
    }
}
