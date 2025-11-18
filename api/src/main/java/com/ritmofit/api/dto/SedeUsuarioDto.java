package com.ritmofit.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SedeUsuarioDto {
    private Long id;
    private Long sedeId;
    private Long usuarioId;
    private String sedeNombre;
    private String sedeDireccion;
}
