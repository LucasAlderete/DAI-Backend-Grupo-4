package com.ritmofit.api.repository;

import com.ritmofit.api.model.entity.Clase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Long> {
    
    @Query("SELECT c FROM Clase c WHERE c.activo = true " +
           "AND (:sedeId IS NULL OR c.sede.id = :sedeId) " +
           "AND (:disciplinaId IS NULL OR c.disciplina.id = :disciplinaId) " +
           "AND (:fechaInicio IS NULL OR c.fechaInicio >= :fechaInicio) " +
           "AND (:fechaFin IS NULL OR c.fechaInicio <= :fechaFin) " +
           "ORDER BY c.fechaInicio ASC")
    Page<Clase> findClasesWithFilters(
            @Param("sedeId") Long sedeId,
            @Param("disciplinaId") Long disciplinaId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable);
    
    @Query("SELECT c FROM Clase c WHERE c.activo = true AND c.fechaInicio >= :fecha ORDER BY c.fechaInicio ASC")
    Page<Clase> findClasesProximas(@Param("fecha") LocalDateTime fecha, Pageable pageable);
}
