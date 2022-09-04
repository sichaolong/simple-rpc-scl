package henu.soft.scl.spi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author sichaolong
 * @date 2022/9/4 22:05
 */
public class SclLoggerService {

    private static final SclLoggerService SERVICE = new SclLoggerService();

    private final SclLogger logger;

    private final List<SclLogger> loggerList;

    private SclLoggerService() {
        ServiceLoader<SclLogger> loader = ServiceLoader.load(SclLogger.class);
        List<SclLogger> list = new ArrayList<>();
        for (SclLogger log : loader) {
            list.add(log);
        }
        // LoggerList 是所有 ServiceProvider
        loggerList = list;
        if (!list.isEmpty()) {
            // Logger 只取一个
            logger = list.get(0);
        } else {
            logger = null;
        }
    }

    /**
     * name : SclLoggerService.java
     * creator : sichaolong
     * date : 2022/9/4 22:08
     * descript : 可以给外界直接使用的静态方法，方便拿到SclLoggerService实例打印日志
    **/

    public static SclLoggerService getService() {
        return SERVICE;
    }

    // 输出日志，调用SPI门面的实现类，也就是其他模块实现SclLogger的类
    public void info(String msg) {
        if (logger == null) {
            System.out.println("info 中没有发现 Logger 服务提供者");
        } else {
            logger.info(msg);
        }
    }
    // 输出日志
    public void debug(String msg) {
        if (loggerList.isEmpty()) {
            System.out.println("debug 中没有发现 Logger 服务提供者");
        }
        loggerList.forEach(log -> log.debug(msg));
    }
}
