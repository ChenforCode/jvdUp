package cn.chenforcode.service;

import soot.SootClass;
import soot.SootMethod;

import java.util.List;

/**
 * @author yumu
 * @date 2023/2/6 22:14
 * @description
 */
public interface SourceService {
    List<SootMethod> getAllSources(List<SootClass> allClasses);
}
