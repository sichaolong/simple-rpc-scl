package henu.soft.scl.rpc.registry;

import henu.soft.scl.extension.SPI;
import henu.soft.scl.rpc.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @author sichaolong
 * @date 2022/7/28 18:30
 */
@SPI
public interface ServiceDiscovery {
    /**
     * lookup service by rpcServiceName
     *
     * @param rpcRequest rpc service pojo
     * @return service address
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
