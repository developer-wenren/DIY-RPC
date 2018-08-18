package com.one.study;

/**
 * ${DESCRIPTION}
 * One on 2018/8/18.
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        System.out.println(Thread.currentThread() + "say Hello" + name);
        return name;
    }
}
