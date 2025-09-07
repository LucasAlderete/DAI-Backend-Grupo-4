package com.ritmofit.api.service;

import com.ritmofit.api.dto.ClaseDTO;
import com.ritmofit.api.dto.ClaseDetalleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface IClaseService {
    Page<ClaseDTO> getClases(Long sedeId, Long disciplinaId, LocalDate fecha, Pageable pageable);
    ClaseDetalleDTO getClaseById(Long id);
}
