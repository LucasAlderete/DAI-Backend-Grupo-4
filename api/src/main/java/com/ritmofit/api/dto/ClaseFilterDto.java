package com.ritmofit.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ClaseFilterDto {
    
    private Long sedeId;
    private Long disciplinaId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer page = 0;
    private Integer size = 20;
}
