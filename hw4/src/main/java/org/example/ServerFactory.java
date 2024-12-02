package org.example;

public interface ServerFactory {
    void listen(int port, Object service);
}
