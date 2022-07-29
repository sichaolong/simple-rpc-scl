## simple-rpc-scl 自己实现一个简易的RPC框架





### 1、概述

一个RPC框架需要具备远程通信方式、通信协议、序列化方式等组件，需要动态代理实现本地存根等。除了基本的远程调用能力，还需要一定的服务治理能力，比如服务的注册发现的注册中心、保护系统流控作用的负载均衡策略、分析错误的链路追踪以及一些基础的日志记录、配置方式等。

将RPC的需求优先级排序，主要分为三步实现：1、实现远程调用相关能力。2、必要的服务治理如注册、发现、负载均衡、路由、容错机制等。3、链路追踪、应用监控、故障告警能力等。

因此可以使用下列组件库完成一个简易的RPC框架：Spring、Netty、log4j、Javassit和Cglib、Zookeeper、curator(最为Zookeeper的客户端实现)、Protostuff。



### 2、实现步骤

### （1）实现远程调用

过程需要：制定RPC协议、制定序列化方法，实现编解码、实现远程通信、实现本地存根

制定RPC协议很重要，主要就是Request、Response消协议头、协议体字段的一些设计，主要关注的就是消息协议体的内容。参考书中作者的设计的Request、Response。从源码中可以看到Request包含的字段是发起一次请求的必须的内容，返回字段包含在Response，在一次RPC请求中，他们不断的被序列化、反序列化。Request、Response类主要在源码中remoting包下

```java
public class Request implements Serializable {

    /**
     * 唯一键
     */
    private String requestId;

    /**
     * 服务名称
     */
    private String className;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 参数类型
     */
    private String[] types;

    /**
     * 参数
     */
    private Object[] args;

    /**
     * 客户端应用名称
     */
    private String clientApplicationName;

    /**
     * 客户端ip
     */
    private String clientIp;

    /**
     * 服务
     */
    private ServiceConfig service;

	 ...
     省略getter、setter等
     ...
}



public class Response implements Serializable {
    private String requestId;
    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 响应结果
     */
    private Object result;

    /**
     * 异常信息
     */
    private Throwable error;

 


```



### （2）定制序列化、编解码方案

请求和响应以什么样的数据格式在网络中传输，可以使用Protostuff进行序列化、反序列化（**将对象转为字节流数组、将字节流数组转为对象**）。使用Encoder、Decoder进行编解码，其中编解码依赖Protostuff，因为采用Netty作为远程通信实现方案，所以在编解码采用Netty的MessageToByteEncoder、ByteToMessageDecoder类，通过自定义Encoder、Decoder类实现他们重写方法内部调用Protostuff完成编解码。相关类主要在源码包serialzation、remoting包下的codec包下。



Decoder、Encoder类

```java
public class Decoder extends ByteToMessageDecoder {
    private Class<?> genericClass;

    public Decoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object obj = ProtostuffSerialization.deserialize(data, genericClass);
        out.add(obj);
    }
}


public class Encoder extends MessageToByteEncoder {

    private Class<?> genericClass;

    public Encoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (genericClass.isInstance(msg)) {
            byte[] data = ProtostuffSerialization.serialize(msg);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}

```

ProtostuffSerialization类

```java
public class ProtostuffSerialization {

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();

    private static Objenesis objenesis = new ObjenesisStd(true);

    private ProtostuffSerialization() {
    }

    /**
     * 从缓存中获取schema，如果没有则创建schema并且缓存schema
     * @param cls
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            // 构建schema的过程可能会比较耗时，因此希望使用过的类对应的schema能被缓存起来
            cachedSchema.put(cls, schema);
        }
        return schema;
    }

    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
        // 获得对象的类
        Class<T> cls = (Class<T>) obj.getClass();
        // 使用LinkedBuffer分配一块默认大小的buffer空间
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            // 通过对象的类构建对应的schema
            Schema<T> schema = getSchema(cls);
            // 使用给定的schema将对象序列化为一个byte数组，并返回
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    public static <T> T deserialize(byte[] data, Class<T> cls) {
        try {
            // 实例化一个类的对象
            T message = objenesis.newInstance(cls);
            // 通过对象的类构建对应的schema
            Schema<T> schema = getSchema(cls);
            // 使用给定的schema将byte数组和对象合并
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
```



### （3）实现远程通信

采用Netty实现，主要是编写server端启动监听方法start、Client端连接connectServer、远程调用remoteCall方法处的逻辑。以及对应的ClientHandler、ServerHandler处理逻辑。除此之外还有连接超时的处理RpcReadTimeoutHandler，相关类主要在remoting包下的netty包下。

目前先使用直连的方式（后面会引进注册中心就不在需要），也就是client写死需要连接的server的port、ip。client需要关注两点，第一是采用直连模式，只需要将配置中配置的server的port、ip映射到ServiceReferenceConfig类的directServerPort、directServerIp即可完成与client的连接与远程通信。第二是client的remoteCall方法是为本地存根提供的，屏蔽了远程调用的细节，也就是调用本地方法转到了remoteCall方法借助Netty实现远程通信。



```java
  // netty-client发起请求到netty-server
    public Response remoteCall(Request request) throws Throwable {

        // 发送请求
        channelFuture.channel().writeAndFlush(request).sync();
        channelFuture.channel().closeFuture().sync();

        // 接收响应
        Response response = clientHandler.getResponse();
        logger.info("receive a response from the server：" + response.getRequestId());

        if (response.getSuccess()) {
            return response;
        }

        throw response.getError();
    }

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private Response response;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.response = (Response) msg;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        ctx.close();
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}

```



### （4）实现本地存根



采用两种方案Javassit复杂方式和Cglib实现动态代理实现本地存根，与Javassit相关的类有：ClassGenerator、Proxy、JavassitProxyFactory、InvokerInvocationHandler。与Cglib相关的类有：CglibProxyFactory、InvokerMethodInterceptor。

简单来说就是本地存根就是在InvokerInvocationHandler、InvokerMethodInterceptor中的invoke方法实现。以InvokerInvocationHandler为例分析，可以看到ServiceReferenceConfig、ServiceInstanceConfig类，这两个是核心类

- ServiceInstanceConfig：封装了服务的port、ip、服务接口、接口实现类等有关服务内容，一个ServiceInstanceConfig实例代表一个服务的实例信息。
- ServiceReferenceConfig：封装了服务引用所需内容，比如需要引用的服务接口，服务实现类等。

```java
@Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invoke(method.getName(), method.getParameterTypes(), args);
    }

    public Object invoke(String methodName, Class[] argTypes, Object[] args) throws Throwable {
        // 同步调用
        return remoteCall(serviceReferenceConfig, methodName, argTypes, args);
    }

    private Object remoteCall(ServiceReferenceConfig refrence, String methodName, Class[] argTypes, Object[] args) throws Throwable {
        // 准备请求参数
        Request request = new Request();
        // 请求id
        request.setRequestId(RpcContextConfig.getUuid().get());
        request.setClientApplicationName(RpcContextConfig.getApplicationName());
        request.setClientIp(RpcContextConfig.getLocalIp());
        // 必要参数
        request.setClassName(serviceReferenceConfig.getName());
        request.setMethodName(methodName);
        request.setTypes(getTypes(argTypes));
        request.setArgs(args);
        Response response;
        try {
            Client client = new Client(refrence);
            ServiceRegistryConfig service = client.connectServer();
            request.setService(service);
            response = client.remoteCall(request);
            return response.getResult();
        } catch (Throwable e) {
            logger.error(String.valueOf(e));
            throw e;
        }
    }


    /**
     * 获取方法的参数类型
     *
     * @param methodTypes
     * @return
     */
    private String[] getTypes(Class<?>[] methodTypes) {
        String[] types = new String[methodTypes.length];
        for (int i = 0; i < methodTypes.length; i++) {
            types[i] = methodTypes[i].getName();
        }
        return types;
    }
```





### （5）实现服务治理

需要增加两个服务治理的能力：服务注册发现、负载均衡。注册发现采用Zookeeper实现，Zookeeper的客户端采用curator-framework实现，新建registry包下的ZookeeperClient类，主要任务是创建微服务注册之后的节点，在创建ServciceChangeListener监听服务Service改变之后进行响应的处理逻辑。

服务注册：ServiceIntsanceConfig服务实例注册的时机是在Bean初始化机制调用的，ServiceIntsanceConfig作为一个Bean，实现了initiallizingBean接口、afterProperties方法，因此在Bean完成初始化之后会调用afterProperties方法，进而调用ZookeeperClient的registerService方法

```java
 /**
     * 发布服务到注册中心
     *
     * @throws Exception
     */
    private void registerService() throws Exception {
        ServiceRegisterProperties register = (ServiceRegisterProperties) SpringUtil.getApplicationContext().getBean("register");
        ServiceInstanceConfig server = (ServiceInstanceConfig) applicationContext.getBean("server");

        this.setPort(server.getPort());

        // zookeeper
        String basePath = "/samples/" + this.getName() + "/provider";
        String path = basePath + "/" + InetAddress.getLocalHost().getHostAddress() + "_" + port;

        ZookeeperClient client = ZookeeperClient.getInstance(register.getIp(), register.getPort());

        client.createPath(basePath);

        this.setIp(InetAddress.getLocalHost().getHostAddress());

        client.saveNode(path, this);
        logger.info("service published successfully: [" + path + "]");
    }
```



服务发现：发生在ServiceReferenceConfig类内，t它内部保存了`List<ServiceInstanceConfig>`服务发现的本质就是从注册中心获取需要引用接口的节点，所以服务发现的本质就是订阅注册中心的注册消息，在ServiceReferenceConfig封装了subscribeServiceChange方法



```java
 /**
     * 订阅服务变化
     */
    private void subscribeServiceChange() {
        ServiceRegisterProperties register = (ServiceRegisterProperties) SpringUtil.getApplicationContext().getBean("register");
        String path = "/samples/" + name + "/provider";
        logger.info("Start subscription service: [" + path + "]");
        // 订阅子目录变化
        ZookeeperClient.getInstance(register.getIp(), register.getPort()).subscribeChildChange(path, new ServiceChangeListener(name));
    }

```



负载均衡：之前在远程通信采用的是直连通信，通过配置ServiceReferenceConfig的directServerIp、directServerPort来完成客户端与服务端的连接，现在如果配置了多个服务端节点也就是集群，需要借助注册中心根据负载均衡算法来选择某个服务来调用。新建loadbalance包，然后主要采用简单的Random进行随机负载LoadBalance、若要更新其他的算法可以在LoadBalancePolicy添加



```java
/**
     * 根据负载均衡策略获取服务
     *
     * @param reference
     * @param loadBalance
     * @return
     * @throws Exception
     */
    public static ServiceInstanceConfig getService(ServiceReferenceConfig reference, String loadBalance) throws Exception {
        List<ServiceInstanceConfig> services = reference.getServices();
        if (services.isEmpty()) {
            throw new RuntimeException("no service available");
        }

        long count = reference.getRefCount();
        count++;
        reference.setRefCount(count);

        if (LoadBalancePolicy.RANDOM.getName().equals(loadBalance)) {
            // 随机
            return random(services);
        }
        return null;
    }

private static ServiceInstanceConfig random(List<ServiceInstanceConfig> services) {
        return services.get(ThreadLocalRandom.current().nextInt(services.size()));
  }
```





### （6）

### 自定义注解方便整合SpringBoot

前面的步骤完成之后，就要考虑整合Spring、SpringBoot使用了，书本中的例子是整合的Spring，通过自定义命名空间，将相关配置写在xml中。为了更加方便，可以利用注解的原理完成Provider服务声明、Consumer服务引用等。

新建annotation包



### 3、测试使用



环境：Windows、Zookeeper 3.4.1、JDK11

新建simple-rpc-scl-test测试Maven工程，然后在创建Provider、Consumer模块，然后需要SpringBoot整合Zookeeper。

- 本地下载启动Zookeeper，下载可视化工具petty-zookeeper。参考：https://www.jianshu.com/p/d7fc9718386e

- 在Provider模块导入Zookeeper的依赖，导入相关的Zookeeper依赖，具体pom和配置参考源码。参考：https://www.jianshu.com/p/67c7d331462f



















