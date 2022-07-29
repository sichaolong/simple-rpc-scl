package henu.soft.scl.rpc.registry;

import henu.soft.scl.extension.SPI;

import java.net.InetSocketAddress;

/**
 * @author sichaolong
 * @date 2022/7/28 18:30
 */
@SPI
public interface ServiceRegistry {
    /**
     * register service
     *
     * @param rpcServiceName    rpc service name
     * @param inetSocketAddress service address
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);

}
