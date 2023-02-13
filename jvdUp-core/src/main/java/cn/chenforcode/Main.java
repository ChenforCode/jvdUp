package cn.chenforcode;

import cn.chenforcode.analysis.AnalysisContext;
import cn.chenforcode.analysis.TaintAnalysis;
import cn.chenforcode.config.SootConfig;
import cn.chenforcode.container.DataContainer;
import cn.chenforcode.service.ClassService;
import cn.chenforcode.service.MethodService;
import cn.chenforcode.service.SourceService;
import cn.chenforcode.service.impl.ClassServiceImpl;
import cn.chenforcode.service.impl.SourceServiceImpl;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.JimpleBody;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yumu
 * @date 2023/2/3 19:35
 * @description
 */
public class Main {

    private static ClassService classService = new ClassServiceImpl();

    private static MethodService methodService;

    public static void main(String[] args) {
//        String jarPath = "D:\\pkucoder\\projects-files\\jvdUp\\commons-collections-3.1.jar";
//        List<String> jarPaths = new ArrayList<>();
//        jarPaths.add("D:\\pkucoder\\projects-files\\jvdUp\\commons-collections-3.1.jar");
//        jarPaths.add("D:\\pkucoder\\projects-files\\jvdUp\\rt.jar");
//        SootConfig.initSootOption(jarPath);
//        DataContainer dataContainer = new DataContainer(jarPaths);
//        SourceService sourceService = new SourceServiceImpl();
//        List<SootMethod> allSources = sourceService.getAllSources(dataContainer.allClasses);
//        for (SootMethod method : allSources) {
//            if (method.getDeclaringClass().getName().contains("AnnotationInvocationHandler")) {
//                JimpleBody body = (JimpleBody) method.retrieveActiveBody();
//                UnitGraph graph = new BriefUnitGraph(body);
//                TaintAnalysis taintAnalysis = new TaintAnalysis(method, graph, new AnalysisContext(method, 1));
//                taintAnalysis.analysis();
//            }
//        }

//        String jarPath = "C:\\pkucoder\\projects\\ideap\\jvdUp\\jvdUp-test\\target\\jvdUp-test-1.0-SNAPSHOT.jar";
        List<String> jarPaths = new ArrayList<>();
//        jarPaths.add("C:\\pkucoder\\projects\\ideap\\jvdUp\\jvdUp-test\\target\\jvdUp-test-1.0-SNAPSHOT.jar");
//        jarPaths.add("D:\\pkucoder\\projects-files\\jvdUp\\rt.jar");
        String jarPath = "D:\\pkucoder\\projects-files\\jvdUp\\commons-collections-3.1.jar";
        jarPaths.add("D:\\pkucoder\\projects-files\\jvdUp\\commons-collections-3.1.jar");
        SootConfig.initSootOption(jarPath);
        DataContainer dataContainer = new DataContainer(jarPaths);
        SourceService sourceService = new SourceServiceImpl();
        for (SootClass allClass : dataContainer.allClasses) {
            for (SootMethod method : allClass.getMethods()) {
                if (!method.isPhantom()) {
                    JimpleBody body = (JimpleBody) method.retrieveActiveBody();
                    UnitGraph graph = new BriefUnitGraph(body);
                    TaintAnalysis taintAnalysis = new TaintAnalysis(method, graph, new AnalysisContext(method, 1));
                    taintAnalysis.analysis();
                }
            }
        }

    }
}
