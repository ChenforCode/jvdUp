package cn.chenforcode.analysis;

import cn.chenforcode.container.DataContainer;
import cn.chenforcode.pojo.entity.JvdMethod;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import soot.SootMethod;
import soot.jimple.JimpleBody;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author yumu
 * @date 2023/2/7 11:26
 * @description
 */
public class MethodAnalysis {
    public void analysisMethod(SootMethod method, AnalysisContext context) {
        //先进行初始化
        if (DataContainer.allMethods.get(method.getSignature()) == null) {
            return;
        }
        DataContainer.methodTaint.put(method.getSignature(), JvdMethod.create(DataContainer.allMethods.get(method.getSignature()), method, Collections.emptySet(), new HashMap<>()));
        try {
            JimpleBody body = (JimpleBody) method.retrieveActiveBody();
            UnitGraph graph = new BriefUnitGraph(body);
            TaintAnalysis taintAnalysis = new TaintAnalysis(method, graph, context);
            taintAnalysis.analysis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        File file = new File("C:\\pkucoder\\projects\\ideap\\jvdUp\\method.json");
        FileReader fileReader = new FileReader(file);
        Set<JvdMethod> set = JSON.parseObject(fileReader.readString(), new TypeReference<Set<JvdMethod>>(){});
        for (JvdMethod jvdMethod : set) {
            if (jvdMethod.getId() == null) {
                System.out.println(JSON.toJSONString(jvdMethod));
                System.out.println("aaaa");
            }
        }

    }
}
