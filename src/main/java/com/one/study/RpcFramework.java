package com.one.study;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

/*
- RPCFramework

- Coding By One ON 08/18
*/
public class RpcFramework {

    /**
     * 服务暴露
     *
     * @param service
     * @param port
     * @throws Exception
     */
    public static void export(final Object service, int port) throws Exception {
        System.out.println("Export Service " + service.getClass().getName() + ", port:" + port);
        ServerSocket serverSocket = new ServerSocket(port);
        for (; ; ) {
            try {
                final Socket socket = serverSocket.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                            String methodName = input.readUTF();
                            try {
                                Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                                Object[] arguments = (Object[]) input.readObject();
                                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

                                try {
                                    Method method = service.getClass().getMethod(methodName, parameterTypes);
                                    Object result = method.invoke(service, arguments);
                                    output.writeObject(result);
                                } catch (Exception e) {
                                    output.writeObject(e);
                                } finally {
                                    output.close();
                                }

                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } finally {
                                input.close();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 服务引用
     *
     * @param interfaceClass
     * @param port
     * @throws Exception
     */
    public static <T> T refer(final Class<T> interfaceClass, final String host, final int port) throws Exception {

        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = new Socket(host, port);
                try {
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    try {
                        output.writeUTF(method.getName());
                        output.writeObject(method.getParameterTypes());
                        output.writeObject(args);
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        try {
                            Object result = input.readObject();
                            if (result instanceof Throwable) {
                                throw (Throwable) result;
                            }
                            return result;
                        } finally {
                            input.close();
                        }
                    } finally {
                        output.close();
                    }
                } finally {
                    socket.close();
                }
            }
        });
    }

    
}
