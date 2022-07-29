package henu.soft.scl.rpc.loadbalance.policy;


import henu.soft.scl.rpc.loadbalance.AbstractLoadBalance;
import henu.soft.scl.rpc.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * name : RandomLoadBalancePolicy.java
 * creator : sichaolong
 * date : 2022/7/29 10:05
 * descript : 随机负均衡策略
**/

public class RandomLoadBalancePolicy extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
