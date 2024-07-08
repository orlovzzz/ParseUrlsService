package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.ConnectionProperties;
import org.example.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@Tag(name = "Connection to server")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping("/config")
    @Operation(summary = "Set ip, port and frequency")
    public ResponseEntity setConnectionProperties(@RequestBody ConnectionProperties connectionProperties) {
        clientService.setConnectionProperties(connectionProperties);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/start")
    @Operation(summary = "Connect to server")
    public ResponseEntity<String> startConnection() {
        try {
            clientService.startConnection();
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stop")
    @Operation(summary = "Disconnect from server")
    public ResponseEntity<String> stopConnection() {
        try {
            clientService.stopConnection();
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/log")
    @Operation(summary = "Create txt file with urls")
    public ResponseEntity logData() {
        clientService.logData();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
