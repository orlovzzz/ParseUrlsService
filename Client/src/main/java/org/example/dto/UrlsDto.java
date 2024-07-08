package org.example.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UrlsDto {
    private String url;
    private String size;
    private LocalDateTime time;
}