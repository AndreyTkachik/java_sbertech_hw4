package org.example;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

public class ClientFactoryImp implements ClientFactory {
    private final String host;
    private final int port;

    public ClientFactoryImp(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public <T> T newClient(Class<T> client) {
        return (T) Proxy.newProxyInstance(
                client.getClassLoader(),
                new Class[]{client},
                new ClientHandler(this.host, this.port)
        );
    }

    private static class ClientHandler implements InvocationHandler {
        private final String host;
        private final int port;

        public ClientHandler(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            try (Socket socket = new Socket(host, port)) {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                out.writeUTF(method.getName());
                out.writeObject(method.getParameterTypes());
                out.writeObject(args);
                out.flush();

                return in.readObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
