package cn.chenforcode.service.impl;

import cn.chenforcode.analysis.AnalysisContext;
import cn.chenforcode.analysis.MethodAnalysis;
import cn.chenforcode.config.SootConfig;
import cn.chenforcode.container.DataContainer;
import cn.chenforcode.service.MainService;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;
import soot.SootMethod;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yumu
 * @date 2023/2/17 21:48
 * @description
 */
@Service
public class MainServiceImpl implements MainService {

    @Override
    public void mainRun() {
        List<String> jarPaths = new ArrayList<>();
        String jarPath = "D:\\pkucoder\\projects-files\\jvdUp\\commons-collections-3.1.jar";
        jarPaths.add("D:\\pkucoder\\projects-files\\jvdUp\\commons-collections-3.1.jar");
        jarPaths.add("D:\\pkucoder\\projects-files\\jvdUp\\rt.jar");
//        jarPaths.add("C:\\pkucoder\\projects\\ideap\\jvdUp\\jvdUp-test\\target\\jvdUp-test-1.0-SNAPSHOT.jar");
        SootConfig.initSootOption(jarPath);
        DataContainer.init(jarPaths);
        List<SootMethod> sources = DataContainer.sources;
        for (SootMethod method : sources) {
            if (!method.isPhantom()) {
                try {
                    new MethodAnalysis().analysisMethod(method, new AnalysisContext(method, 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        File file = new File("method.json");
        if (file.exists()) {
            file.delete();
        }
        FileUtil.touch(file);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.append(JSONUtil.toJsonStr(DataContainer.methodTaint.values()));

        file = new File("callEdge.json");
        if (file.exists()) {
            file.delete();
        }
        FileUtil.touch(file);
        fileWriter = new FileWriter(file);
        fileWriter.append(JSONUtil.toJsonStr(DataContainer.callEdges));

        file = new File("subCallEdge.json");
        if (file.exists()) {
            file.delete();
        }
        FileUtil.touch(file);
        fileWriter = new FileWriter(file);
        fileWriter.append(JSONUtil.toJsonStr(DataContainer.subCallEdges));

        System.out.println("method size: " + DataContainer.methodTaint.size());
        System.out.println("call size: " + DataContainer.callEdges.size());
        System.out.println("subCall size: " + DataContainer.subCallEdges.size());
    }

    public static void main(String[] args) {
        MainServiceImpl mainService = new MainServiceImpl();
        mainService.mainRun();
    }
}
