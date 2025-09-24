package com.ritmofit.api.repository;

import com.ritmofit.api.model.entity.Reserva;
import com.ritmofit.api.model.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRespository extends JpaRepository<Reserva, Long> {
    
    @Query("SELECT r FROM Reserva r WHERE r.usuario = :usuario ORDER BY r.clase.fechaInicio DESC")
    Page<Reserva> findByUsuarioOrderByFechaInicioDesc(@Param("usuario") Usuario usuario, Pageable pageable);
    
    @Query("SELECT r FROM Reserva r WHERE r.usuario = :usuario AND r.clase.fechaInicio >= :fecha ORDER BY r.clase.fechaInicio ASC")
    List<Reserva> findProximasReservas(@Param("usuario") Usuario usuario, @Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT r FROM Reserva r WHERE r.usuario = :usuario AND r.clase.id = :claseId AND r.estado = 'CONFIRMADA'")
    Reserva findReservaActiva(@Param("usuario") Usuario usuario, @Param("claseId") Long claseId);
    
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.clase.id = :claseId AND r.estado = 'CONFIRMADA'")
    Long countReservasActivasPorClase(@Param("claseId") Long claseId);
}
