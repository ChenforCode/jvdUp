package cn.chenforcode.pojo.entity;

/**
 * @author yumu
 * @date 2023/2/14 16:27
 * @description
 */
public class JvdClass {
    private Long id;
    private String name;
    private String superClass;
    private boolean isPhantom = false;
    private boolean isInterface = false;
    private boolean hasSuperClass = false;
    private boolean hasInterfaces = false;
    private boolean hasDefaultConstructor = false;
    private boolean isInitialed = false;
    private boolean isSerializable = false;
    private boolean isAbstract = false;
}
