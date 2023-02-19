package cn.chenforcode.pojo.nodes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author yumu
 * @date 2023/2/6 20:31
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallEdge {
    private String id;
    private String sourceId;
    private String sourceName;
    private String targetId;
    private String targetName;
    private Map<Integer, Integer> interTaintInfo;
}
