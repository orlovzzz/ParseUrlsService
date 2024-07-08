package org.example.service;

import org.example.controller.ServerController;
import org.example.interfaces.ConfigService;
import org.example.interfaces.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

@Service
public class ConfigServiceImpl implements ConfigService {

    private int serverPort = 80;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ServerController server;

    @Override
    public void configPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void startListenPort() {
        try {
            server = new ServerController(new InetSocketAddress("localhost", serverPort),
                    applicationContext.getBean(ServerService.class), applicationContext.getBean(WebSocketSaver.class));
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopListenPort() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
