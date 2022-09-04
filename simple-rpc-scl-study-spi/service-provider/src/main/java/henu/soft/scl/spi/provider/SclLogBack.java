package henu.soft.scl.spi.provider;

import henu.soft.scl.spi.service.SclLogger;

/**
 * @author sichaolong
 * @date 2022/9/4 22:27
 */
public class SclLogBack implements SclLogger {
    @Override
    public void info(String s) {
        System.out.println(s);
    }

    @Override
    public void debug(String s) {
        System.out.println(s);

    }
}
