package henu.soft.scl.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * name : RpcConfigEnum.java
 * creator : sichaolong
 * date : 2022/7/28 18:13
 * descript : 配置文件前缀
**/

@AllArgsConstructor
@Getter
public enum RpcConfigEnum {

    RPC_CONFIG_PATH("rpc.properties"),
    ZK_ADDRESS("rpc.zookeeper.address");

    private final String propertyValue;

}
