package com.ritmofit.api.repository;

import com.ritmofit.api.model.entity.Asistencia;
import com.ritmofit.api.model.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    
    @Query("SELECT a FROM Asistencia a " +
           "JOIN FETCH a.clase c " +
           "LEFT JOIN FETCH c.sede " +
           "LEFT JOIN FETCH c.disciplina " +
           "LEFT JOIN FETCH c.instructor " +
           "WHERE a.usuario = :usuario " +
           "AND (:fechaInicio IS NULL OR a.fechaAsistencia >= :fechaInicio) " +
           "AND (:fechaFin IS NULL OR a.fechaAsistencia <= :fechaFin) " +
           "ORDER BY a.fechaAsistencia DESC")
    Page<Asistencia> findAsistenciasConFiltros(
            @Param("usuario") Usuario usuario,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable);
    
    @Query("SELECT a FROM Asistencia a WHERE a.usuario = :usuario AND a.clase.id = :claseId")
    Asistencia findByUsuarioAndClase(@Param("usuario") Usuario usuario, @Param("claseId") Long claseId);
    
    @Query("SELECT a FROM Asistencia a WHERE a.usuario = :usuario " +
           "AND a.fechaCheckin >= :fechaMinima " +
           "AND a.fechaCheckin <= :fechaMaxima " +
           "AND a.calificacion IS NULL " +
           "ORDER BY a.fechaCheckin DESC")
    Page<Asistencia> findAsistenciasCalificables(
            @Param("usuario") Usuario usuario,
            @Param("fechaMinima") LocalDateTime fechaMinima,
            @Param("fechaMaxima") LocalDateTime fechaMaxima,
            Pageable pageable);
}
