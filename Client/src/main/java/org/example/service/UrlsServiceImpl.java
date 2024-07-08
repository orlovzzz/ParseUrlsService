package org.example.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.example.dto.UrlsDto;
import org.example.entity.Urls;
import org.example.interfaces.UrlsMapper;
import org.example.interfaces.UrlsService;
import org.example.repository.UrlsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UrlsServiceImpl implements UrlsService {

    @Autowired
    private UrlsRepository urlsRepository;
    @Autowired
    private UrlsMapper urlsMapper;

    @PreDestroy
    private void deleteFromTable() {
        urlsRepository.deleteAll();
    }

    @Override
    public void addUrl(UrlsDto urlDto) {
        Urls url = urlsMapper.fromDto(urlDto);
        urlsRepository.save(url);
    }

    @Override
    public List<Urls> getUrls() {
        return urlsRepository.findAll();
    }
}