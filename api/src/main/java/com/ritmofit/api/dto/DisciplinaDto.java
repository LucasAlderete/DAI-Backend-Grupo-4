package com.ritmofit.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DisciplinaDto {
    
    private Long id;
    private String nombre;
    private String descripcion;
}
