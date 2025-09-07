package com.ritmofit.api.dto;

import com.ritmofit.api.dto.SedeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaseDetalleDTO {
    private Long id;
    private DisciplinaDTO disciplina;
    private LocalDateTime fechaHora;
    private int duracionMinutos;
    private int cupoDisponible;
    private String nombreInstructor;
    private SedeDTO sede;
    private String direccionSede;
}
