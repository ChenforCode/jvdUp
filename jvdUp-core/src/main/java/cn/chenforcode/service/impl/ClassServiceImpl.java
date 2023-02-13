package cn.chenforcode.service.impl;

import cn.chenforcode.config.SootConfig;
import cn.chenforcode.service.ClassService;
import org.springframework.stereotype.Service;

/**
 * @author yumu
 * @date 2023/2/3 19:38
 * @description
 */
@Service
public class ClassServiceImpl implements ClassService {


    public static void main(String[] args) {
        String jarPath = "D:\\pkucoder\\projects-files\\jvdUp\\commons-collections-3.1.jar";
        SootConfig.initSootOption(jarPath);
//        ClassServiceImpl classService = new ClassServiceImpl();
//        List<SootClass> allClasses = classService.getAllClasses(jarPath);
//        SootClass sootClass = Scene.v().getSootClass("org.apache.commons.collections.map.TransformedMap");
//        List<SootClass> allFather = classService.getAllFather(sootClass);


    }
}
