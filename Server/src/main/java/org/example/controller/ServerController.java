package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.websocket.*;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.example.dto.FindUrls;
import org.example.dto.MouseMovement;
import org.example.interfaces.ServerService;
import org.example.service.WebSocketSaver;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@NoArgsConstructor
public class ServerController extends WebSocketServer {

    @Autowired
    private ServerService serverService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private WebSocketSaver webSocketSaver;
    private long currentTime;
    private boolean isBeenFiveSecondStop;
    private boolean flag;
    private String size;
    private final ExecutorService searchUrlExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService mouseMoveExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService mouseScrollExecutor = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void postConstruct() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @PreDestroy
    private void preDestroy() {
        searchUrlExecutor.shutdown();
        mouseMoveExecutor.shutdown();
        mouseMoveExecutor.shutdown();
    }

    public ServerController(InetSocketAddress address, ServerService serverService, WebSocketSaver webSocketSaver) {
        super(address);
        this.serverService = serverService;
        this.webSocketSaver = webSocketSaver;
    }

    public void sendMessage(String message, WebSocket webSocket) {
        try {
            System.out.println(message);
            webSocket.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error send response: " + message);
        }
    }

    @Scheduled(fixedRate = 500)
    public void scheduleSendFindUrls() {
        if (webSocketSaver.webSocket != null && webSocketSaver.webSocket.isOpen()) {
            FindUrls findUrls = serverService.searchNotSendUrls();
            if (findUrls != null) {
                try {
                    sendMessage(objectMapper.writeValueAsString(findUrls), webSocketSaver.webSocket);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    @OnOpen
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        webSocketSaver.webSocket = webSocket;
        size = serverService.openChrome();
        sendMessage(size, webSocket);
    }

    @Override
    @OnClose
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        conn.close();
        flag = false;
        isBeenFiveSecondStop = false;
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    @OnMessage
    @SneakyThrows
    public void onMessage(WebSocket conn, String message) {
        if (!flag) {
            currentTime = System.currentTimeMillis();
            flag = true;
        }
        if (isBeenFiveSecondStop || System.currentTimeMillis() - currentTime <= 10000) {
            MouseMovement mouseMovement = objectMapper.readValue(message, MouseMovement.class);
            handleMessage(mouseMovement);
        } else {
            sendMessage("stop", conn);
            serverService.resize();
            Thread.sleep(5000);
            isBeenFiveSecondStop = true;
            sendMessage(serverService.getChromeSize(), conn);
        }
    }

    private void handleMessage(MouseMovement mouseMovement) {
        mouseMoveExecutor.execute(() -> {
            serverService.mouseMove(mouseMovement.getX(), mouseMovement.getY());
        });
        mouseScrollExecutor.execute(() -> {
            serverService.mouseScroll(mouseMovement.getScroll());
        });
        searchUrlExecutor.execute(() -> {
            serverService.searchUrl();
        });
    }

    @Override
    @OnError
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("Error on connection " + conn.getRemoteSocketAddress() + ": " + ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("Server start on: " + this.getAddress());
    }
}