package henu.soft.scl.rpc.provider;

import henu.soft.scl.rpc.config.RpcServiceConfig;

/**
 * @author sichaolong
 * @date 2022/7/29 10:23
 */

/**
 * name : ServiceProvider.java
 * creator : sichaolong
 * date : 2022/7/29 10:24
 * descript : 将Provider服务提供者的服务发布出去，方案可选，
 *  如 zookeeper、nacos等
**/

public interface ServiceProvider {

    /**
     * @param rpcServiceConfig rpc service related attributes
     */
    void addService(RpcServiceConfig rpcServiceConfig);

    /**
     * @param rpcServiceName rpc service name
     * @return service object
     */
    Object getService(String rpcServiceName);

    /**
     * @param rpcServiceConfig rpc service related attributes
     */
    void publishService(RpcServiceConfig rpcServiceConfig);

}
