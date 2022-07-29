package henu.soft.scl.utils;

/**
 * name : StringUtil.java
 * creator : sichaolong
 * date : 2022/7/28 18:20
 * descript :
**/

public class StringUtil {

    public static boolean isBlank(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
