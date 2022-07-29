package henu.soft.scl.utils;

import java.util.Collection;

/**
 * name : CollectionUtil.java
 * creator : sichaolong
 * date : 2022/7/28 18:19
 * descript : 集合工具类，判断是否为空
**/

public class CollectionUtil {

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

}
