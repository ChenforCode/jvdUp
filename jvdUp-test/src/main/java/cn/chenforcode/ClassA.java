package cn.chenforcode;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;

import java.io.File;

/**
 * @author yumu
 * @date 2023/2/7 16:33
 * @description
 */
public class ClassA {

    public int funcA(int args) {
        int var1 = args;
        ClassB classB = new ClassB();
        int var2 = funcB(var1);
        return var2;
    }

    public int funcB(int args) {
        return args;
    }

    public Object funcC(Object obj) {
        obj = new Object();
        return obj;
    }

    public static void main(String[] args) {
        File file = new File("a.txt");
        if (file.exists()) {
            file.delete();
        }
        FileUtil.touch(file);
        FileWriter fileWriter = new FileWriter(file);
    }
}
