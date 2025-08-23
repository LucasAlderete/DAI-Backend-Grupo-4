package com.ritmofit.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class ReservaDto {
    private Long id;
    private String clase;
    private String disciplina;
    private String horario;
    private String profesor;
    private String sede;
    private Date fecha;
}

