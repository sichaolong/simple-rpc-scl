package henu.soft.scl.spi.service;

/**
 * @author sichaolong
 * @date 2022/9/4 22:10
 */
public class TestMain {
    public static void main(String[] args) {
        SclLoggerService loggerService = SclLoggerService.getService();

        loggerService.info("Hello SPI");
        loggerService.debug("Hello SPI");

        /**
         * 输出：
         * info 中没有发现 Logger 服务提供者
         * debug 中没有发现 Logger 服务提供者
         *
         * 加入SclLogBack实现之后输出：
         * Hello SPI
         * Hello SPI
         */
    }
}
