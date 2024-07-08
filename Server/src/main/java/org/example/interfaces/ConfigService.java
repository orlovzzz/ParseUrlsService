package org.example.interfaces;

public interface ConfigService {
    void configPort(int serverPort);
    void startListenPort();
    void stopListenPort();
}
