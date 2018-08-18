package com.one.study;

/*
- 

- Coding By One ON 08/18
*/
public class RPCConsumer {
    public static void main(String[] args) throws Exception {
        HelloService service = RpcFramework.refer(HelloService.class, "127.0.0.1", 1234);
        for (int i = 0; i < 10000; i++) {
            String one = service.sayHello("One" + i);
            System.out.println("rpc result :" + one);
            Thread.sleep(1000);
        }
    }
}
