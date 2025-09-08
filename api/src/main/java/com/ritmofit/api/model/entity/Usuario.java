package com.ritmofit.api.model.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // atributos temporales
    private String nombre;
    private String email;
    private String foto; // URL opcional

}
