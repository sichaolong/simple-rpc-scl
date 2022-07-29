package henu.soft.scl.rpc.config;

import lombok.*;


/**
 * name : RpcServiceConfig.java
 * creator : sichaolong
 * date : 2022/7/29 10:21
 * descript : 当一个接口有多个实现类，需要给ServiceImpl加上version、group区分
**/

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceConfig {
    /**
     * service version
     */
    private String version = "";
    /**
     * when the interface has multiple implementation classes, distinguish by group
     */
    private String group = "";

    /**
     * target service
     */
    private Object service;

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }
}
