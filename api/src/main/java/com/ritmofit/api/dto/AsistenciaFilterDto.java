package com.ritmofit.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AsistenciaFilterDto {
    
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer page = 0;
    private Integer size = 20;
}
