package henu.soft.scl.provider;

import henu.soft.scl.api.HelloService;
import henu.soft.scl.provider.impl.HelloServiceImpl;
import henu.soft.scl.rpc.annotation.SclRpcScan;
import henu.soft.scl.rpc.config.RpcServiceConfig;
import henu.soft.scl.rpc.remoting.transport.netty.server.NettyRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author sichaolong
 * @date 2022/7/29 14:05
 */
@SclRpcScan(basePackage = {"henu.soft"})
public class NettyServerMain {
    public static void main(String[] args) {
        // Register service via annotation
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        // Register service manually
        System.out.println("1==============");

        HelloService helloService = new HelloServiceImpl();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                .group("test2").version("version2").service(helloService).build();
        System.out.println("2==============");

        nettyRpcServer.registerService(rpcServiceConfig);
        nettyRpcServer.start();
    }
}
