package com.ritmofit.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SedeDto {
    
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
}
