package henu.soft.scl.rpc.loadbalance;

import henu.soft.scl.extension.SPI;
import henu.soft.scl.rpc.remoting.dto.RpcRequest;

import java.util.List;

/**
 * @author sichaolong
 * @date 2022/7/28 18:35
 */
@SPI
public interface LoadBalance {
    /**
     * Choose one from the list of existing service addresses list
     *
     * @param serviceUrlList Service address list
     * @param rpcRequest
     * @return target service address
     */
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
