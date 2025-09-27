package com.ritmofit.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UsuarioDtoUpdate {

    private String nombre;
    private String email;
    private String password;
    private String fotoUrl;
}
