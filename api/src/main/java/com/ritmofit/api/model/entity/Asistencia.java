package com.ritmofit.api.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "asistencias")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clase_id", nullable = false)
    private Clase clase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @Column(name = "fecha_asistencia", nullable = false)
    private LocalDateTime fechaAsistencia;

    @Column(name = "fecha_checkin", nullable = false)
    private LocalDateTime fechaCheckin;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    @Column(name = "calificacion")
    private Integer calificacion;

    @Column(name = "comentario", length = 500)
    private String comentario;

    @Column(name = "fecha_calificacion")
    private LocalDateTime fechaCalificacion;
}
