package com.ritmofit.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ClaseDto {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private DisciplinaDto disciplina;
    private InstructorDto instructor;
    private SedeDto sede;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer cupoMaximo;
    private Integer cupoActual;
    private Boolean disponible;
}
