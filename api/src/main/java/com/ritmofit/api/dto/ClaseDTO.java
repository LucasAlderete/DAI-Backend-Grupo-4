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
public class ClaseDTO {
    private Long id;
    private DisciplinaDTO disciplina;
    private LocalDateTime fechaHora;
    private String nombreInstructor;
    private SedeDTO sede;
}
