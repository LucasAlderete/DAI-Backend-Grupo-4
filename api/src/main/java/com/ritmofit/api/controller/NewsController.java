package com.ritmofit.api.controller;

import com.ritmofit.api.dto.NewsDto;
import com.ritmofit.api.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@Tag(name = "News", description = "Noticias y promociones para Home")
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    @Operation(summary = "Listar news", description = "Devuelve una lista ordenada de noticias y promociones para la Home")
    public ResponseEntity<List<NewsDto>> list() {
        return ResponseEntity.ok(newsService.list());
    }
}
