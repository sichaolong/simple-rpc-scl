package henu.soft.scl.rpc.remoting.transport;

import henu.soft.scl.extension.SPI;
import henu.soft.scl.rpc.remoting.dto.RpcRequest;

/**
 * @author sichaolong
 * @date 2022/7/29 10:31
 */
@SPI
public interface RpcRequestTransport {
    /**
     * send rpc request to server and get result
     *
     * @param rpcRequest message body
     * @return data from server
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}

