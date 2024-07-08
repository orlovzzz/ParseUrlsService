package org.example.connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.websocket.*;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.example.dto.UrlsDto;
import org.example.dto.WindowSize;
import org.example.interfaces.ClientService;
import org.example.interfaces.ServerConnection;
import org.example.interfaces.UrlsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Getter
@Setter
@ClientEndpoint
@Component
public class ServerConnectionImpl implements ServerConnection {
    private String ip = "localhost";
    private int port = 80;
    private Session session;
    @Autowired
    private ApplicationContext applicationContext;
    private final List<Future<Void>> futures = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UrlsService urlsService;

    @PostConstruct
    public void postConstruct() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @PreDestroy
    public void preDestroy() {
        futures.forEach(o -> o.cancel(true));
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected");
    }

    @OnMessage
    public void onMessage(String message) throws JsonProcessingException {
        System.out.println(message);
        if (message.contains("top") && message.contains("bottom")) {
            handleMessage(message);
        }
        if (message.contains("stop")) {
            System.out.println("STOP");
            futures.forEach(o -> o.cancel(true));
        }
        if (message.contains("url")) {
            UrlsDto dto = objectMapper.readValue(message, UrlsDto.class);
            urlsService.addUrl(dto);
        }
    }

    private void handleMessage(String message) throws JsonProcessingException {
        ClientService clientService = applicationContext.getBean(ClientService.class);
        clientService.setWindowSize(objectMapper.readValue(message, WindowSize.class));
        futures.add(clientService.sendMouseMovement());
    }

    @SneakyThrows
    @OnClose
    public void OnClose(Session session) {
        applicationContext.getBean(ClientService.class).stopConnection();
        System.out.println("Close connection");
    }

    @SneakyThrows
    public void connect() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            URI uri = new URI("ws://" + ip + ":" + port);
            container.connectToServer(this, uri);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
