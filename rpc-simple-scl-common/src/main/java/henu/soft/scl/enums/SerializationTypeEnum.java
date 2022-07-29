package henu.soft.scl.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * name : SerializationTypeEnum.java
 * creator : sichaolong
 * date : 2022/7/28 18:14
 * descript : 序列化选取枚举类
**/

@AllArgsConstructor
@Getter
public enum SerializationTypeEnum {

    KYRO((byte) 0x01, "kyro"),
    PROTOSTUFF((byte) 0x02, "protostuff"),
    HESSIAN((byte) 0X03, "hessian");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (SerializationTypeEnum c : SerializationTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

}
