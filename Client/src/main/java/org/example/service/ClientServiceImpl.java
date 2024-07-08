package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.SneakyThrows;
import org.example.dto.ConnectionProperties;
import org.example.dto.MouseMovement;
import org.example.dto.WindowSize;
import org.example.entity.Urls;
import org.example.interfaces.ClientService;
import org.example.interfaces.ServerConnection;
import org.example.interfaces.UrlsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@Setter
@EnableAsync
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ServerConnection serverConnection;
    private int frequency = 50;

    private WindowSize windowSize;

    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private boolean isConnected;

    @Autowired
    private UrlsService urlsService;

    @Override
    public void setConnectionProperties(ConnectionProperties connectionProperties) {
        serverConnection.setIp(connectionProperties.getIp());
        serverConnection.setPort(connectionProperties.getPort());
        this.frequency = connectionProperties.getFrequency();
    }

    @Override
    public void startConnection() {
        try {
            serverConnection.connect();
            isConnected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopConnection() {
        try {
            serverConnection.disconnect();
            isConnected = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logData() {
        List<Urls> urls = urlsService.getUrls();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("logData.txt"))){
            for (Urls url : urls) {
                writer.write(url.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    @Async
    public Future<Void> sendMouseMovement() {
        while (isConnected) {
            MouseMovement mm = createMouseMovementMessage();
            serverConnection.sendMessage(objectMapper.writeValueAsString(mm));
            Thread.sleep(1000 / frequency);
        }
        return CompletableFuture.completedFuture(null);
    }

    private MouseMovement createMouseMovementMessage() {
        int x = random.nextInt(windowSize.getRight() - windowSize.getLeft() + 1) + windowSize.getLeft();
        int y = random.nextInt(windowSize.getBottom() - windowSize.getTop() + 1) + windowSize.getTop();
        int scroll = -1 + random.nextInt(3);
        MouseMovement mouseMovement = new MouseMovement(x, y, scroll);
        return mouseMovement;
    }
}
