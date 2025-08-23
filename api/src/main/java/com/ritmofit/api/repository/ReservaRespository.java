package com.ritmofit.api.repository;

import com.ritmofit.api.model.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRespository extends JpaRepository<Reserva, Long> {
}
