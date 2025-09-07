package com.ritmofit.api.repository;

import com.ritmofit.api.model.entity.Clase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Long> {

    @Query("SELECT c FROM Clase c WHERE " +
           "(:sedeId IS NULL OR c.sede.id = :sedeId) AND " +
           "(:disciplinaId IS NULL OR c.disciplina.id = :disciplinaId) AND " +
           "(:fecha is NULL OR FUNCTION ('DATE', c.fechaHora) = :fecha")
    Page<Clase> findByFilters(@Param("sedeId") Long sedeId,
                                @Param("disciplinaId") Long disciplinaId,
                                @Param("fecha") LocalDate fecha,
                                Pageable pageable);
}
