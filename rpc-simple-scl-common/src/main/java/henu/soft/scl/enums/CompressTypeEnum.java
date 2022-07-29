package henu.soft.scl.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * name : CompressTypeEnum.java
 * creator : sichaolong
 * date : 2022/7/28 18:12
 * descript : 压缩枚举类
**/

@AllArgsConstructor
@Getter
public enum CompressTypeEnum {

    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

}
