package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.interfaces.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Config Server")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @GetMapping("/config")
    @Operation(summary = "Change server port")
    public ResponseEntity setPort(@RequestParam(value = "port") String port) {
        configService.configPort(Integer.parseInt(port));
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/start")
    @Operation(summary = "Start server")
    public ResponseEntity start() {
        try {
            configService.startListenPort();
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stop")
    @Operation(summary = "Stop server")
    public ResponseEntity stop() {
        try {
            configService.stopListenPort();
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}