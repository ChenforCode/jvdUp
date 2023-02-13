package cn.chenforcode.service.impl;

import cn.chenforcode.service.SourceService;
import soot.SootClass;
import soot.SootMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yumu
 * @date 2023/2/6 22:16
 * @description
 */
public class SourceServiceImpl implements SourceService {
    @Override
    public List<SootMethod> getAllSources(List<SootClass> allClasses) {
        List<SootMethod> sources = new ArrayList<>();
        for (SootClass sootClass :allClasses) {
            for (SootMethod method : sootClass.getMethods()) {
                if (method.getName().contains("readObject")) {
                    sources.add(method);
                }
            }
        }
        return sources;
    }
}
