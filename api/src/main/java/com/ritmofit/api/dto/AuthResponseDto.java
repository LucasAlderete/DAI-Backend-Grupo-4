package com.ritmofit.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponseDto {
    private Long id;
    private String token;
    private String email;
    private String nombre;
    private String password;
    private String fotoUrl;
    private boolean nuevoUsuario;
    private String mensaje;
}
