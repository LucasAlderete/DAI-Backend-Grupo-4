package com.ritmofit.api.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "instructores")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String apellido;

    @Column
    private String email;

    @Column
    private String telefono;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
