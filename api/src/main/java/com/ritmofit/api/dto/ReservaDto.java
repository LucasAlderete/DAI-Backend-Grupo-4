package com.ritmofit.api.dto;

import com.ritmofit.api.model.entity.Reserva;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter 
@Setter
@Builder
public class ReservaDto {
    private Long id;
    private Long usuarioId;
    private Long claseId;
    private ClaseDto clase;
    private LocalDateTime fechaReserva;
    private Reserva.EstadoReserva estado;
    private LocalDateTime fechaCancelacion;
}

