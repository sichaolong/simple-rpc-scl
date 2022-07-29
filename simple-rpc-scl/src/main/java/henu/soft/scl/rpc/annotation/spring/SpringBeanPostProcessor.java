package henu.soft.scl.rpc.annotation.spring;

import henu.soft.scl.extension.ExtensionLoader;
import henu.soft.scl.factory.SingletonFactory;
import henu.soft.scl.rpc.annotation.SclRpcReference;
import henu.soft.scl.rpc.annotation.SclRpcService;
import henu.soft.scl.rpc.config.RpcServiceConfig;
import henu.soft.scl.rpc.provider.ServiceProvider;
import henu.soft.scl.rpc.provider.impl.ZkServiceProviderImpl;
import henu.soft.scl.rpc.proxy.RpcClientProxy;
import henu.soft.scl.rpc.remoting.transport.RpcRequestTransport;
import lombok.SneakyThrows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author sichaolong
 * @date 2022/7/28 16:56
 */

@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SpringBeanPostProcessor.class);

    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
    }

    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(SclRpcService.class)) {
            logger.info("[{}] is annotated with  [{}]", bean.getClass().getName(), SclRpcService.class.getCanonicalName());
            // get RpcService annotation
            SclRpcService rpcService = bean.getClass().getAnnotation(SclRpcService.class);
            // build RpcServiceProperties
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean).build();
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            SclRpcReference rpcReference = declaredField.getAnnotation(SclRpcReference.class);
            if (rpcReference != null) {
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
        return bean;
    }
}
