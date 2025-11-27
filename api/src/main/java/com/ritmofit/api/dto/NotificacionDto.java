package com.ritmofit.api.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificacionDto {
    private Long id;
    private Long claseId;
    private String tipo;
    private String mensaje;
    private String fechaInicio;
    private Long usuarioId; 
}
