package org.example.interfaces;

import org.example.dto.UrlsDto;
import org.example.entity.Urls;

import java.util.List;

public interface UrlsService {
    void addUrl(UrlsDto url);
    List<Urls> getUrls();
}
