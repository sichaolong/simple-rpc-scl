package henu.soft.scl.spi.service;

/**
 * @author sichaolong
 * @date 2022/9/4 22:05
 */
/**
 * name : SclLogger.java
 * creator : sichaolong
 * date : 2022/9/4 22:06
 * descript : SPI接口，自定义日志门面，只定义接口，交给其他模块实现
**/

public interface SclLogger {
    void info(String msg);

    void debug(String msg);
}
