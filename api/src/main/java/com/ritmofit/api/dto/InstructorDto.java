package com.ritmofit.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InstructorDto {
    
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
}
