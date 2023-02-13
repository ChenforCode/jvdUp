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

}
