package cn.chenforcode.analysis;

import lombok.Data;
import soot.Unit;
import soot.Value;

/**
 * @author yumu
 * @date 2023/2/7 10:35
 * @description
 */
@Data
public class TaintInfo {
    private Unit source;
    private Value var;
    private boolean isArgs;
    private Integer argsIndex;
    private boolean isField;
    private boolean isSource;
}
