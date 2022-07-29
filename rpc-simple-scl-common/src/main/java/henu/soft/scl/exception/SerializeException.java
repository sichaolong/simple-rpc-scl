package henu.soft.scl.exception;

/**
 * name : SerializeException.java
 * creator : sichaolong
 * date : 2022/7/28 18:15
 * descript : 序列化异常
**/

public class SerializeException extends RuntimeException {
    public SerializeException(String message) {
        super(message);
    }
}
