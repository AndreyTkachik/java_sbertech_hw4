package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executors;

public class ServerFactoryImp implements ServerFactory {
    @Override
    public void listen(int port, Object service) {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port);
                 var executor = Executors.newVirtualThreadPerTaskExecutor()
            ){
                serverSocket.setSoTimeout(2_000);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    executor.submit(() -> handleOperation(clientSocket, service));
                }
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void handleOperation(Socket clientSocket, Object service) {
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

            String methodName = in.readUTF();
            Class<?>[] paramTypes = (Class<?>[]) in.readObject();
            Object[] args = (Object[]) in.readObject();

            try {
                Method method = service.getClass().getMethod(methodName, paramTypes);
                Object result = method.invoke(service, args);

                out.writeObject(result);
            } catch (Exception e) {
                out.writeObject(e);
            }
            out.flush();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}