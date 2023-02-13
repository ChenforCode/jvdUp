package cn.chenforcode.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import soot.SootMethod;

/**
 * @author yumu
 * @date 2023/2/7 12:00
 * @description 函数分析的上下文
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisContext {
    private SootMethod method;
    private Integer depth;
}
