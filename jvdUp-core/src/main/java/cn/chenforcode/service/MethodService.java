package cn.chenforcode.service;

import soot.SootMethod;

import java.util.List;

/**
 * @author yumu
 * @date 2023/2/3 19:38
 * @description
 */
public interface MethodService {
    List<SootMethod> getAllMethods(String path);
}
