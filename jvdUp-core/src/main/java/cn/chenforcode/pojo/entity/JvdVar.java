package cn.chenforcode.pojo.entity;

import cn.chenforcode.constant.JvdVarEnum;
import javafx.scene.shape.VLineTo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import soot.Local;
import soot.SootField;
import soot.Value;
import soot.jimple.internal.JimpleLocal;

/**
 * @author yumu
 * @date 2023/2/7 22:48
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JvdVar {
    private Value value;
    private SootField sootField;
    private JvdVarEnum jvdVarEnum;

    public static JvdVar createValue(Value value, JvdVarEnum jvdVarEnum) {
        return new JvdVar(value);
    }

    public static JvdVar createField(SootField sootField, JvdVarEnum jvdVarEnum) {
        return new JvdVar(sootField);
    }

    public JvdVar(Value value) {
        this.value = value;
        this.jvdVarEnum = JvdVarEnum.SOOT_LOCAL;
    }

    public JvdVar(SootField field) {
        this.sootField = field;
        this.jvdVarEnum = JvdVarEnum.SOOT_FIELD;
    }

}
