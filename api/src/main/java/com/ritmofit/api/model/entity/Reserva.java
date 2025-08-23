package com.ritmofit.api.model.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "reservas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //atributos temporales hasta modelar bien las otras entidades
    private String clase;
    private String disciplina;
    private String horario;
    private String profesor;
    private String sede;
    private Date fecha;

}
