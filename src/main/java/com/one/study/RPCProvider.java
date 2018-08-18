package com.one.study;

/**
 * @author One
 */ /*
- 服务提供者

- Coding By One ON 08/18
*/
public class RPCProvider {
    public static void main(String[] args) throws Exception {
        HelloService helloService = new HelloServiceImpl();
        RpcFramework.export(helloService,1234);
    }
}
