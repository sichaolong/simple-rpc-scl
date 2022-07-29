package henu.soft.scl.consumer;

import henu.soft.scl.api.HelloService;
import henu.soft.scl.rpc.annotation.SclRpcReference;
import org.springframework.stereotype.Component;

/**
 * @author sichaolong
 * @date 2022/7/29 14:53
 */
@Component
public class HelloController {

    @SclRpcReference(version = "version2", group = "test2")
    private HelloService helloService;

    public void test() throws InterruptedException {
        String hello = this.helloService.sayHello("sichaolong");
        //如需使用 assert 断言，需要在 VM options 添加参数：-ea
        // assert "Hello description is 222".equals(hello);
        Thread.sleep(12000);
        for (int i = 0; i < 10; i++) {
            System.out.println(helloService.sayHello("scl"));
        }
    }
}
