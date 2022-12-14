package henu.soft.scl.rpc.remoting.transport.netty.server;

import henu.soft.scl.factory.SingletonFactory;
import henu.soft.scl.rpc.config.CustomShutdownHook;
import henu.soft.scl.rpc.config.RpcServiceConfig;
import henu.soft.scl.rpc.provider.ServiceProvider;
import henu.soft.scl.rpc.provider.impl.ZkServiceProviderImpl;
import henu.soft.scl.rpc.remoting.transport.netty.codec.RpcMessageDecoder;
import henu.soft.scl.rpc.remoting.transport.netty.codec.RpcMessageEncoder;
import henu.soft.scl.rpc.remoting.transport.netty.handler.NettyRpcServerHandler;
import henu.soft.scl.utils.RuntimeUtil;
import henu.soft.scl.utils.concurrent.threadpool.ThreadPoolFactoryUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author sichaolong
 * @date 2022/7/29 10:33
 */
@Slf4j
@Component
public class NettyRpcServer {

    public static final int PORT = 9998;

    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);

    public void registerService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }

    @SneakyThrows
    public void start() {
        CustomShutdownHook.getCustomShutdownHook().clearAll();
        String host = InetAddress.getLocalHost().getHostAddress();

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                RuntimeUtil.cpus() * 2,
                ThreadPoolFactoryUtil.createThreadFactory("service-handler-group", false)
        );
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // TCP??????????????? Nagle ????????????????????????????????????????????????????????????????????????????????????TCP_NODELAY ??????????????????????????????????????? Nagle ?????????
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // ???????????? TCP ??????????????????
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //????????????????????????????????????????????????????????????????????????????????????,????????????????????????????????????????????????????????????????????????????????????????????????
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // ???????????????????????????????????????????????????????????????
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 30 ?????????????????????????????????????????????????????????
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            p.addLast(new RpcMessageEncoder());
                            p.addLast(new RpcMessageDecoder());
                            p.addLast(serviceHandlerGroup, new NettyRpcServerHandler());
                        }
                    });

            // ???????????????????????????????????????
            ChannelFuture f = b.bind(host, PORT).sync();        System.out.println("2==============");

            // ?????????????????????????????????
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("occur exception when start server:", e);
        } finally {
            log.error("shutdown bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }
    }


}
