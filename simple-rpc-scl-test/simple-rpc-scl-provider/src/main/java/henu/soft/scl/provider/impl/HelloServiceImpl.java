package henu.soft.scl.provider.impl;

import henu.soft.scl.api.HelloService;
import henu.soft.scl.rpc.annotation.SclRpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@SclRpcService(group = "test2", version = "version2")
public class HelloServiceImpl implements HelloService {

    static {
        log.info("HelloServiceImpl被创建");
    }


    @Override
    public String sayHello(String name) {
        log.info("远程调用 HelloServiceImpl: {}.Hello ,", name);


        return name;

    }
}
