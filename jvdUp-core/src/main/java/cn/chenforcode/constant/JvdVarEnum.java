package cn.chenforcode.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author yumu
 * @date 2023/2/7 22:49
 * @description
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum JvdVarEnum {
    SOOT_LOCAL(1, "普通变量"),
    SOOT_FIELD(2, "类成员变量")
    ;
    private int id;
    private String desc;
}
