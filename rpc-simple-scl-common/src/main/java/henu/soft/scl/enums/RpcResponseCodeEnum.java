package henu.soft.scl.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * name : RpcResponseCodeEnum.java
 * creator : sichaolong
 * date : 2022/7/28 18:14
 * descript : 消息响应结果状态枚举类
**/

@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseCodeEnum {

    SUCCESS(200, "The remote call is successful"),
    FAIL(500, "The remote call is fail");
    private final int code;

    private final String message;

}
