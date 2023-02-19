package cn.chenforcode.endpoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author yumu
 * @date 2023/2/14 17:18
 * @description
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum EndpointType {
    DEFAULT(0, "未知"),
    SOURCE(1, "污染源"),
    SINK(2, "危险函数"),
    KNOW(3, "已知传播函数")
    ;
    private int id;
    private String desc;
}
