package cn.chenforcode.pojo.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import soot.SootMethod;

import java.util.List;

/**
 * @author yumu
 * @date 2023/2/6 20:31
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallEdge {
    private SootMethod source;
    private SootMethod target;
    private List<Integer> interTaintInfo;
}
