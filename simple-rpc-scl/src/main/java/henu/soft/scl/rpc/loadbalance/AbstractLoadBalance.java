package henu.soft.scl.rpc.loadbalance;

import henu.soft.scl.rpc.remoting.dto.RpcRequest;
import henu.soft.scl.utils.CollectionUtil;

import java.util.List;

/**
 * @author sichaolong
 * @date 2022/7/28 18:36
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String selectServiceAddress(List<String> serviceAddresses, RpcRequest rpcRequest) {
        if (CollectionUtil.isEmpty(serviceAddresses)) {
            return null;
        }
        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses, rpcRequest);
    }

    protected abstract String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest);

}
