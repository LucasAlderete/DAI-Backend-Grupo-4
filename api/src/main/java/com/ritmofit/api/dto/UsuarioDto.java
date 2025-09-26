package com.ritmofit.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UsuarioDto {
    
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    private String fotoUrl;

    // Campos opcionales para exponer
    private Boolean activo;
    private Boolean emailVerificado;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoAcceso;
}
