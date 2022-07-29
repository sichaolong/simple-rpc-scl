package henu.soft.scl.consumer;

import henu.soft.scl.api.HelloService;
import henu.soft.scl.rpc.annotation.SclRpcScan;
import henu.soft.scl.rpc.config.RpcServiceConfig;
import henu.soft.scl.rpc.proxy.RpcClientProxy;
import henu.soft.scl.rpc.remoting.transport.RpcRequestTransport;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author sichaolong
 * @date 2022/7/29 14:51
 */
@SclRpcScan(basePackage = {"henu.soft"})
public class NettyClientMain {
    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController helloController = (HelloController) applicationContext.getBean("helloController");
        helloController.test();
    }
}

