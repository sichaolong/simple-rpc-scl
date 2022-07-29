package henu.soft.scl.utils;
/**
 * name : RuntimeUtil.java
 * creator : sichaolong
 * date : 2022/7/28 18:20
 * descript :
**/

public class RuntimeUtil {
    /**
     * 获取CPU的核心数
     *
     * @return cpu的核心数
     */
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}
