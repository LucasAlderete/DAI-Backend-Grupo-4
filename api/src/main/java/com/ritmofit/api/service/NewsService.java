package com.ritmofit.api.service;

import com.ritmofit.api.dto.NewsDto;
import com.ritmofit.api.model.entity.News;
import com.ritmofit.api.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public List<NewsDto> list() {
        return newsRepository.findAllByOrderByOrdenAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private NewsDto toDto(News n) {
        return NewsDto.builder()
                .id(n.getId())
                .titulo(n.getTitulo())
                .descripcion(n.getDescripcion())
                .orden(n.getOrden())
                .imgUrl(n.getImgUrl())
                .build();
    }
}
