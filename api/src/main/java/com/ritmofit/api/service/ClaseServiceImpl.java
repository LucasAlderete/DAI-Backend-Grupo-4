package com.ritmofit.api.service;

import com.ritmofit.api.dto.ClaseDTO;
import com.ritmofit.api.dto.ClaseDetalleDTO;
import com.ritmofit.api.dto.DisciplinaDTO;
import com.ritmofit.api.dto.SedeDTO;
import com.ritmofit.api.model.entity.Clase;
import com.ritmofit.api.repository.ClaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ClaseServiceImpl implements IClaseService {

    @Autowired
    private ClaseRepository claseRepository;

    @Override
    public Page<ClaseDTO> getClases(Long sedeId, Long disciplinaId, LocalDateTime fecha, Pageable pageable) {
        Page<Clase> clases = claseRepository.findByFilters(sedeId, disciplinaId, fecha, pageable);
        return clases.map(this::mapToClaseDTO);
    }

    @Override
    public ClaseDetalleDTO getClaseById(Long id) {
        Clase clase = claseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada con id: " + id));
        return mapToClaseDetalleDTO(clase);
    }

    private ClaseDTO mapToClaseDTO(Clase clase) {
        SedeDTO sedeDTO = SedeDTO.builder()
                .id(clase.getSede().getId())
                .nombre(clase.getSede().getNombre())
                .build();

        DisciplinaDTO disciplinaDTO = DisciplinaDTO.builder()
                .id(clase.getDisciplina().getId())
                .nombre(clase.getDisciplina().getNombre())
                .build();

        return ClaseDTO.builder()
                .id(clase.getId())
                .disciplina(disciplinaDTO)
                .fechaHora(clase.getFechaHora())
                .nombreInstructor(clase.getInstructor().getNombre())
                .sede(sedeDTO)
                .build();
    }

    private ClaseDetalleDTO mapToClaseDetalleDTO(Clase clase) {
        SedeDTO sedeDTO = SedeDTO.builder()
                .id(clase.getSede().getId())
                .nombre(clase.getSede().getNombre())
                .build();

        DisciplinaDTO disciplinaDTO = DisciplinaDTO.builder()
                .id(clase.getDisciplina().getId())
                .nombre(clase.getDisciplina().getNombre())
                .build();

        return ClaseDetalleDTO.builder()
                .id(clase.getId())
                .disciplina(disciplinaDTO)
                .fechaHora(clase.getFechaHora())
                .duracionMinutos(clase.getDuracionMinutos())
                .cupoDisponible(clase.getCupoDisponible())
                .nombreInstructor(clase.getInstructor().getNombre())
                .sede(sedeDTO)
                .direccionSede(clase.getSede().getDireccion())
                .build();
    }
}
