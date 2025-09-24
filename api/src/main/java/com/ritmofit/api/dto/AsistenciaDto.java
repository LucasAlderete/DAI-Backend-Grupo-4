package com.ritmofit.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AsistenciaDto {
    
    private Long id;
    private Long usuarioId;
    private Long claseId;
    private ClaseDto clase;
    private LocalDateTime fechaAsistencia;
    private LocalDateTime fechaCheckin;
    private Integer duracionMinutos;
    private Integer calificacion;
    private String comentario;
    private LocalDateTime fechaCalificacion;
}
