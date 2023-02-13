package cn.chenforcode.analysis;

import soot.SootMethod;
import soot.jimple.JimpleBody;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

/**
 * @author yumu
 * @date 2023/2/7 11:26
 * @description
 */
public class MethodAnalysis {
    public void analysisMethod(SootMethod method, AnalysisContext context) {
        JimpleBody body = (JimpleBody) method.retrieveActiveBody();
        UnitGraph graph = new BriefUnitGraph(body);
        TaintAnalysis taintAnalysis = new TaintAnalysis(method, graph, context);
        taintAnalysis.analysis();
    }
}
