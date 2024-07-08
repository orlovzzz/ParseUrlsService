package org.example.interfaces;

import org.example.dto.UrlsDto;
import org.example.entity.Urls;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UrlsMapper {
    Urls fromDto(UrlsDto dto);
}
