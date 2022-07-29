package henu.soft.scl.rpc.config;

import java.util.UUID;


public class RpcContextConfig {
    private static ThreadLocal<String> uuid = ThreadLocal.withInitial(()-> UUID.randomUUID().toString());

    private static String applicationName;

    private static String localIp;


    public static String getApplicationName() {
        return applicationName;
    }

    public static void setApplicationName(String applicationName) {
        RpcContextConfig.applicationName = applicationName;
    }

    public static String getLocalIp() {
        return localIp;
    }

    public static void setLocalIp(String localIp) {
        RpcContextConfig.localIp = localIp;
    }

    public static ThreadLocal<String> getUuid() {
        return uuid;
    }

    public static void setUuid(ThreadLocal<String> uuid) {
        RpcContextConfig.uuid = uuid;
    }

}
