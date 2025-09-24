package com.ritmofit.api.service;

import com.ritmofit.api.dto.*;
import com.ritmofit.api.model.entity.*;
import com.ritmofit.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClaseService {

    private final ClaseRepository claseRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final SedeRepository sedeRepository;
    private final InstructorRepository instructorRepository;

    public Page<ClaseDto> obtenerClasesConFiltros(ClaseFilterDto filtros) {
        Pageable pageable = PageRequest.of(filtros.getPage(), filtros.getSize());
        
        Page<Clase> clases = claseRepository.findClasesWithFilters(
                filtros.getSedeId(),
                filtros.getDisciplinaId(),
                filtros.getFechaInicio(),
                filtros.getFechaFin(),
                pageable);
        
        return clases.map(this::convertirAClaseDto);
    }

    public ClaseDto obtenerClasePorId(Long id) {
        Clase clase = claseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));
        
        return convertirAClaseDto(clase);
    }

    public List<DisciplinaDto> obtenerDisciplinas() {
        return disciplinaRepository.findByActivoTrueOrderByNombre()
                .stream()
                .map(this::convertirADisciplinaDto)
                .collect(Collectors.toList());
    }

    public List<SedeDto> obtenerSedes() {
        return sedeRepository.findByActivoTrueOrderByNombre()
                .stream()
                .map(this::convertirASedeDto)
                .collect(Collectors.toList());
    }

    public List<InstructorDto> obtenerInstructores() {
        return instructorRepository.findByActivoTrueOrderByNombre()
                .stream()
                .map(this::convertirAInstructorDto)
                .collect(Collectors.toList());
    }

    private ClaseDto convertirAClaseDto(Clase clase) {
        return ClaseDto.builder()
                .id(clase.getId())
                .nombre(clase.getNombre())
                .descripcion(clase.getDescripcion())
                .disciplina(convertirADisciplinaDto(clase.getDisciplina()))
                .instructor(convertirAInstructorDto(clase.getInstructor()))
                .sede(convertirASedeDto(clase.getSede()))
                .fechaInicio(clase.getFechaInicio())
                .fechaFin(clase.getFechaFin())
                .cupoMaximo(clase.getCupoMaximo())
                .cupoActual(clase.getCupoActual())
                .disponible(clase.getCupoActual() < clase.getCupoMaximo())
                .build();
    }

    private DisciplinaDto convertirADisciplinaDto(Disciplina disciplina) {
        return DisciplinaDto.builder()
                .id(disciplina.getId())
                .nombre(disciplina.getNombre())
                .descripcion(disciplina.getDescripcion())
                .build();
    }

    private InstructorDto convertirAInstructorDto(Instructor instructor) {
        return InstructorDto.builder()
                .id(instructor.getId())
                .nombre(instructor.getNombre())
                .apellido(instructor.getApellido())
                .email(instructor.getEmail())
                .telefono(instructor.getTelefono())
                .build();
    }

    private SedeDto convertirASedeDto(Sede sede) {
        return SedeDto.builder()
                .id(sede.getId())
                .nombre(sede.getNombre())
                .direccion(sede.getDireccion())
                .telefono(sede.getTelefono())
                .build();
    }
}
