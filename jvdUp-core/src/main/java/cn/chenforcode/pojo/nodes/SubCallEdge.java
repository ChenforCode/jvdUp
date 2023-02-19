package cn.chenforcode.pojo.nodes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author yumu
 * @date 2023/2/6 20:32
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubCallEdge {
    private String id;
    private String sourceId;
    private String sourceName;
    private String targetId;
    private String targetName;
    private String originTargetId;
    private String originTargetName;
    private Map<Integer, Integer> interTaintInfo;
}
