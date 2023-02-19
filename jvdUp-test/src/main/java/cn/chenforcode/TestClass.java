package cn.chenforcode;

/**
 * @author yumu
 * @date 2023/2/11 18:45
 * @description
 */
public class TestClass {
    private Object object = new Object();

    public void func1() {
        ClassA classA = new ClassA();
        classA.funcC(object);
    }

    public Object func2() {
        return object;
    }

    public void func3() {
        Object test = new Object();
        object = test;
    }
}
