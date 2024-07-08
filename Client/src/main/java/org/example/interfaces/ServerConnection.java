package org.example.interfaces;

public interface ServerConnection {
    void connect();
    void disconnect();
    void setIp(String ip);
    void setPort(int port);
    void sendMessage(String message);
}
