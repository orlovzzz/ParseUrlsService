package org.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectionProperties {

    private String ip;
    private int port;
    private int frequency;

}