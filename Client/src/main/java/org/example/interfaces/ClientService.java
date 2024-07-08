package org.example.interfaces;

import org.example.dto.ConnectionProperties;
import org.example.dto.WindowSize;

import java.util.concurrent.Future;

public interface ClientService {
    void setConnectionProperties(ConnectionProperties connectionProperties);
    void startConnection();
    void stopConnection();
    void logData();
    Future<Void> sendMouseMovement();
    void setWindowSize(WindowSize windowSize);
}
