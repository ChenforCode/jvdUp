package cn.chenforcode.container;

import cn.chenforcode.endpoint.Endpoints;
import cn.chenforcode.pojo.entity.JvdMethod;
import cn.chenforcode.pojo.nodes.CallEdge;
import cn.chenforcode.pojo.nodes.SubCallEdge;
import cn.hutool.core.util.IdUtil;
import org.springframework.util.CollectionUtils;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SourceLocator;

import java.util.*;

/**
 * @author yumu
 * @date 2023/2/6 17:15
 * @description
 */
public class DataContainer {
    public static List<SootClass> allClasses;
    public static List<SootMethod> sources;
    public static List<JvdMethod> sinks;
    public static Map<SootClass, Set<SootClass>> subClassToClass;
    public static Map<SootClass, Set<SootClass>> classToSubClass;
    public static Set<CallEdge> callEdges;
    public static Set<SubCallEdge> subCallEdges;
    public static Map<String, JvdMethod> methodTaint;
    public static Map<String, String> allMethods;


    public static void init(List<String> jarPaths) {
        allClasses = new ArrayList<>();
        classToSubClass = new HashMap<>();
        subClassToClass = new HashMap<>();
        callEdges = new HashSet<>();
        subCallEdges = new HashSet<>();
        methodTaint = new HashMap<>();
        sources = new ArrayList<>();
        allMethods = new HashMap<>();
        initAllClass(jarPaths);
        initSubClassToClass();
        initClassToSubClass();
        System.out.println();
    }


    private static void initAllClass(List<String> jarPaths) {
        for (String jarPath : jarPaths) {
            for (String cl : SourceLocator.v().getClassesUnder(jarPath)) {
                SootClass sootClass = Scene.v().loadClassAndSupport(cl);
                if (!sootClass.isPhantom()) {
//                    if (sootClass.getName().contains("org.apache") || sootClass.getName().contains("com.sun") ||sootClass.getName().contains("java.util")) {
//                        allClasses.add(sootClass);
//                    }
//                    sootClass.setApplicationClass();
                    allClasses.add(sootClass);
                    for (SootMethod method : sootClass.getMethods()) {
                        if (Endpoints.isSource(method.getName())) {
                            sources.add(method);
                        }
                        if (!allMethods.containsKey(method.getSignature())) {
                            allMethods.put(method.getSignature(), IdUtil.fastUUID());
                        }
                    }
                }
            }
        }
    }

    public static void initSubClassToClass() {
        for (SootClass sootClass : allClasses) {
            Set<SootClass> allFather = getAllFather(sootClass);
            if (!CollectionUtils.isEmpty(allFather)) {
                subClassToClass.put(sootClass, allFather);
            }
        }
    }

    private static void initClassToSubClass() {
        for (SootClass curClass : allClasses) {
            for (Map.Entry<SootClass, Set<SootClass>> entry : subClassToClass.entrySet()) {
                for (SootClass sootClass : entry.getValue()) {
                    if (curClass.getName().equals(sootClass.getName())) {
                        if (classToSubClass.containsKey(curClass)) {
                            classToSubClass.get(curClass).add(entry.getKey());
                        } else {
                            Set<SootClass> subClass = new HashSet<>();
                            subClass.add(entry.getKey());
                            classToSubClass.put(curClass, subClass);
                        }
                    }
                }
            }
        }
    }

    private static Set<SootClass> getAllFather(SootClass curClass) {
        Set<SootClass> fathers = new HashSet<>();
        if (curClass.hasSuperclass() && !curClass.getSuperclass().getName().equals("java.lang.Object")) {
            fathers.add(curClass.getSuperclass());
            fathers.addAll(getAllFather(curClass.getSuperclass()));
        }
        if (curClass.getInterfaceCount() > 0) {
            for (SootClass superInterface : curClass.getInterfaces()) {
                fathers.add(superInterface);
                fathers.addAll(getAllFather(superInterface));
            }
        }
        return fathers;
    }

}
