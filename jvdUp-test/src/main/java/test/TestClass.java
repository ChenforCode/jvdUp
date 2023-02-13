package test;

/**
 * @author yumu
 * @date 2023/2/11 18:45
 * @description
 */
public class TestClass {
    public void main(int argMain) {
        int var = argMain;
        int funcARet = funcA(var);
        funcB(funcARet);
    }

    public int funcA(int argA) {
        int var = argA + 1;
        return var;
    }

    public void funcB(int argB) {
        int var = argB + 1;
    }
}

class ClassB {

}
