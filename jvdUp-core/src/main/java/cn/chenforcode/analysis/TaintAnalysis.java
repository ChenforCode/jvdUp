package cn.chenforcode.analysis;


import cn.chenforcode.MapUtil;
import cn.chenforcode.container.DataContainer;
import cn.chenforcode.pojo.entity.JvdMethod;
import cn.chenforcode.pojo.nodes.CallEdge;
import cn.chenforcode.pojo.nodes.SubCallEdge;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.XmlUtil;
import org.springframework.util.CollectionUtils;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.util.Chain;

import java.io.File;
import java.util.*;

import static cn.chenforcode.container.DataContainer.methodTaint;
import static cn.chenforcode.endpoint.Endpoints.knowsMap;

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
    private Map<SootField, Set<Integer>> fieldMap;
    private Map<String, Map<Integer, Integer>> interTaint;
    private Value retVal;

    public TaintAnalysis(SootMethod method, DirectedGraph<Unit> graph, AnalysisContext context) {
        super(graph);
        this.method = method;
        this.context = context;
        this.taintMap = new HashMap<>();
        this.fieldMap = new HashMap<>();
        this.interTaint = new HashMap<>();
        System.out.println(graph);
    }

    @Override
    protected void flowThrough(Map<Value, TaintInfo> in, Unit unit, Map<Value, TaintInfo> out) {
        if (!isInit) {
            List<Local> methodArgs = method.getActiveBody().getParameterLocals();
            if (!CollectionUtils.isEmpty(methodArgs)) {
                for (Local arg : methodArgs) {
                    Set<Integer> set = new HashSet<>();
                    set.add(methodArgs.indexOf(arg) + 1);
                    taintMap.put(arg, set);
                }
            }
            Chain<SootField> fields = method.getDeclaringClass().getFields();
            if (!CollectionUtils.isEmpty(fields)) {
                for (SootField field : fields) {
                    //成员变量都属于this参数，即0参
                    Set<Integer> set = new HashSet<>();
                    set.add(0);
                    fieldMap.put(field, set);
                }
            }
            //初始化完毕
            isInit = true;
        }
        Stmt stmt = (Stmt) unit;
        System.out.println("Cur analysis: " + stmt);
        processStmt(stmt);
    }

    public void analysis() {
        super.doAnalysis();
        //分析完毕之后，保存这个函数的分析结果
        Set<Integer> ret = new HashSet<>();
        if (this.retVal != null) {
            if (taintMap.containsKey(this.retVal)) {
                ret.addAll(taintMap.get(this.retVal));
            }
        }
        if (DataContainer.allMethods.get(method.getSignature()) == null) {
            return;
        }
        methodTaint.put(method.getSignature(), JvdMethod.create(DataContainer.allMethods.get(method.getSignature()), method, ret, interTaint));
    }

    //递归函数
    private void processInvoke(InvokeExpr invokeExpr, Boolean know, Value base) {
        SootMethod target = invokeExpr.getMethod();
        //开始构建此次调用的一个call边关系
        //获取本次调用的所有参数，来
        List<Value> args = invokeExpr.getArgs();
        Map<Integer, Integer> callTaint = new HashMap<>();
        if (taintMap.containsKey(base)) {
            //base受到谁的影响 -> 传递到调用函数的this
            callTaint.put(new ArrayList<>(taintMap.get(base)).get(0), 0);
        }
        for (int i = 0; i < args.size(); i++) {
            Value arg = args.get(i);
            if (taintMap.containsKey(arg)) {
                callTaint.put(new ArrayList<>(taintMap.get(arg)).get(0), i + 1);
            }
        }
        if (context.getDepth() >= 15) {
            return;
        }
        if (target.getParameterCount() == 0 && !taintMap.containsKey(base)) {
            return;
        }
        //把target也进行初始化
        interTaint.put(target.getName(), callTaint);
        CallEdge callEdge = CallEdge.builder()
                .id(IdUtil.fastUUID())
                .sourceId(DataContainer.allMethods.get(method.getSignature()))
                .sourceName(method.getSignature())
                .targetId(DataContainer.allMethods.get(target.getSignature()))
                .targetName(target.getSignature())
                .interTaintInfo(callTaint).build();
        DataContainer.callEdges.add(callEdge);
        if (know) {
            return;
        }

        if (!methodTaint.containsKey(target.getSignature())) {
            if (!(method.isPhantom() || method.isAbstract() || method.isNative() || method.isStatic())) {
                if (DataContainer.allMethods.get(method.getSignature()) == null) {
                    return;
                }
                DataContainer.methodTaint.put(target.getSignature(), JvdMethod.create(DataContainer.allMethods.get(method.getSignature()), target, Collections.emptySet(), new HashMap<>()));
                new MethodAnalysis().analysisMethod(target, new AnalysisContext(target, context.getDepth() + 1));
            }
        }

        //分析子类
        Set<SootClass> subClasses = DataContainer.classToSubClass.get(target.getDeclaringClass());
        if (!CollectionUtils.isEmpty(subClasses)) {
            for (SootClass subClass : subClasses) {
                //重新获取子类中的方法进行分析
                if (subClass.declaresMethod(target.getSubSignature())) {
                    SootMethod subMethod = subClass.getMethod(target.getSubSignature());
                    interTaint.put(subMethod.getName(), callTaint);
                    SubCallEdge subCallEdge = SubCallEdge.builder()
                            .id(IdUtil.fastUUID())
                            .sourceId(DataContainer.allMethods.get(method.getSignature()))
                            .sourceName(method.getSignature())
                            .targetId(DataContainer.allMethods.get(subMethod.getSignature()))
                            .targetName(subMethod.getSignature())
                            .originTargetId(DataContainer.allMethods.get(target.getSignature()))
                            .originTargetName(target.getSignature())
                            .interTaintInfo(callTaint).build();
                    DataContainer.subCallEdges.add(subCallEdge);
                    if (!methodTaint.containsKey(subMethod.getSignature())) {
                        if (!(method.isPhantom() || method.isAbstract() || method.isNative() || method.isStatic())) {
                            if (DataContainer.allMethods.get(method.getSignature()) == null) {
                                return;
                            }
                            DataContainer.methodTaint.put(subMethod.getSignature(), JvdMethod.create(DataContainer.allMethods.get(method.getSignature()), subMethod, Collections.emptySet(), new HashMap<>()));
                            new MethodAnalysis().analysisMethod(subMethod, new AnalysisContext(subMethod, context.getDepth() + 1));
                        }
                    }
                }
            }
        }
    }

    private void processStmt(Stmt stmt) {
        if (stmt instanceof JInvokeStmt) {
            InvokeExpr invokeExpr = stmt.getInvokeExpr();
            String methodName = invokeExpr.getMethod().getName();
            if (methodName.contains("Exception")) {
                return;
            }
            Boolean know = false;
            if (knowsMap.containsKey(methodName)) {
                know = true;
            }
            Value base = null;
            if (invokeExpr instanceof SpecialInvokeExpr) {
                base = ((SpecialInvokeExpr) invokeExpr).getBase();
            } else if (invokeExpr instanceof VirtualInvokeExpr) {
                base = ((VirtualInvokeExpr) invokeExpr).getBase();
            } else if (invokeExpr instanceof InterfaceInvokeExpr) {
                base = ((InterfaceInvokeExpr) invokeExpr).getBase();
            }
            processInvoke(invokeExpr, know, base);
        } else if (stmt instanceof AssignStmt) {
            //赋值语句
            AssignStmt assignStmt = (AssignStmt) stmt;
            Value leftOp = assignStmt.getLeftOp();
            Value rightOp = assignStmt.getRightOp();
            processAssign(leftOp, rightOp);
        } else if (stmt instanceof JIdentityStmt) {
            // 参数 -> 变量的绑定语句
            JIdentityStmt jIdentityStmt = (JIdentityStmt) stmt;
            Value leftOp = jIdentityStmt.getLeftOp();
            Value rightOp = jIdentityStmt.getRightOp();
            Set<Integer> set = new HashSet<>();
            if (rightOp instanceof ThisRef) {
                if (!taintMap.containsKey(rightOp)) {
                    set.add(0);
                    taintMap.put(rightOp, set);
                }
                if (leftOp instanceof JInstanceFieldRef) {
                    MapUtil.addOrInitByFieldMap(fieldMap, leftOp, taintMap.get(rightOp));
                } else {
                    MapUtil.addOrInitByTaintMap(taintMap, leftOp, taintMap.get(rightOp));
                }
            }
        } else if (stmt instanceof ReturnStmt) {
            // return 语句
            ReturnStmt returnStmt = (ReturnStmt) stmt;
            this.retVal = returnStmt.getOp();
        }
    }

    private void processAssign(Value leftOp, Value rightOp) {
        if (rightOp instanceof InvokeExpr) {
            //赋值型函数调用
            InvokeExpr invokeExpr = (InvokeExpr) rightOp;
            if (invokeExpr.getMethod().getName().contains("Exception")) {
                return;
            }
            Value base = null;
            if (invokeExpr instanceof SpecialInvokeExpr) {
                base = ((SpecialInvokeExpr) invokeExpr).getBase();
            } else if (invokeExpr instanceof VirtualInvokeExpr) {
                base = ((VirtualInvokeExpr) invokeExpr).getBase();
            } else if (invokeExpr instanceof InterfaceInvokeExpr) {
                base = ((InterfaceInvokeExpr) invokeExpr).getBase();
            }
            String name = invokeExpr.getMethod().getName();
            JvdMethod jvdMethod;
            if (knowsMap.containsKey(name)) {
                processInvoke(invokeExpr, true, base);
                jvdMethod = knowsMap.get(name);
            } else {
                processInvoke(invokeExpr, false, base);
                jvdMethod = methodTaint.get(invokeExpr.getMethod());
            }
            if (jvdMethod == null) {
                return;
            }
            Set<Integer> taintToRet = jvdMethod.getTaintToRet();
            if (!CollectionUtils.isEmpty(taintToRet)) {
                //index指，target中的第几个函数会影响
                for (Integer index : taintToRet) {
                    //这个代表了target中的函数'
                    Value arg = null;
                    if (index == 0) {
                        arg = base;
                    } else {
                        arg = invokeExpr.getArg(index - 1);
                    }
                    if (taintMap.containsKey(arg)) {
                        Set<Integer> set = taintMap.get(arg);
                        if (leftOp instanceof JInstanceFieldRef) {
                            MapUtil.addOrInitByFieldMap(fieldMap, leftOp, set);
                        } else {
                            MapUtil.addOrInitByTaintMap(taintMap, leftOp, set);
                        }
                    }
                }
            }
        } else if (rightOp instanceof JimpleLocal) {
            //普通的右值赋值，如果右值在污点里
            if (taintMap.containsKey(rightOp)) {
                if (leftOp instanceof JInstanceFieldRef) {
                    MapUtil.addOrInitByFieldMap(fieldMap, leftOp, taintMap.get(rightOp));
                } else {
                    MapUtil.addOrInitByTaintMap(taintMap, leftOp, taintMap.get(rightOp));
                }
            }
        } else if (rightOp instanceof StaticFieldRef) {
            // 静态变量的右侧赋值

        } else if (rightOp instanceof JCastExpr) {
            Value value = ((JCastExpr) rightOp).getOp();
            if (taintMap.containsKey(value)) {
                if (leftOp instanceof JInstanceFieldRef) {
                    MapUtil.addOrInitByFieldMap(fieldMap, leftOp, taintMap.get(value));
                } else {
                    MapUtil.addOrInitByTaintMap(taintMap, leftOp, taintMap.get(value));
                }
            }
        } else if (rightOp instanceof JInstanceFieldRef) {
            SootField rightField = ((JInstanceFieldRef) rightOp).getField();
            if (fieldMap.containsKey(rightField)) {
                if (leftOp instanceof JInstanceFieldRef) {
                    MapUtil.addOrInitByFieldMap(fieldMap, leftOp, fieldMap.get(rightField));
                } else {
                    MapUtil.addOrInitByTaintMap(taintMap, leftOp, fieldMap.get(rightField));
                }
            }
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
}
