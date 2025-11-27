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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClaseService {

    private final ClaseRepository claseRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final SedeRepository sedeRepository;
    private final InstructorRepository instructorRepository;
    private final NotificacionService notificacionService;

    // ----------------------------------------------------------
    // ✔ MÉTODO QUE FALTABA Y ROMPÍA EL CONTROLLER
    // ----------------------------------------------------------
    public Page<ClaseDto> obtenerClasesConFiltros(ClaseFilterDto filtros) {
        Pageable pageable = PageRequest.of(filtros.getPage(), filtros.getSize());
        Page<Clase> clases = claseRepository.findClasesWithFilters(
                filtros.getSedeId(),
                filtros.getDisciplinaId(),
                filtros.getFechaInicio(),
                filtros.getFechaFin(),
                pageable
        );
        return clases.map(this::convertirAClaseDto);
    }

    // ✔ Otro método que el controller pedía
    public ClaseDto obtenerClasePorId(Long id) {
        Clase clase = claseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));
        return convertirAClaseDto(clase);
    }

    public List<DisciplinaDto> obtenerDisciplinas() {
        return disciplinaRepository.findByActivoTrueOrderByNombre()
                .stream().map(this::convertirADisciplinaDto).toList();
    }

    public List<SedeDto> obtenerSedes() {
        return sedeRepository.findByActivoTrueOrderByNombre()
                .stream().map(this::convertirASedeDto).toList();
    }

    public List<InstructorDto> obtenerInstructores() {
        return instructorRepository.findByActivoTrueOrderByNombre()
                .stream().map(this::convertirAInstructorDto).toList();
    }

    // ----------------------------------------------------------
    // ✔ ACTUALIZAR CLASE (incluye CANCELADA y REPROGRAMADA)
    // ----------------------------------------------------------
    @Transactional
    public Clase actualizarClase(Long id, Clase claseNueva) {

        Clase antes = claseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        // Copia para comparar después
        Clase original = new Clase();
        original.setFechaInicio(antes.getFechaInicio());
        original.setFechaFin(antes.getFechaFin());
        original.setSede(antes.getSede());
        original.setInstructor(antes.getInstructor());
        original.setDisciplina(antes.getDisciplina());
        original.setNombre(antes.getNombre());
        original.setDescripcion(antes.getDescripcion());
        original.setActivo(antes.isActivo());

        // ✔ Detectamos cancelación
        boolean cancelada = antes.isActivo() && !claseNueva.isActivo();

        // Aplicamos cambios
        antes.setActivo(claseNueva.isActivo());
        antes.setNombre(claseNueva.getNombre());
        antes.setDescripcion(claseNueva.getDescripcion());
        antes.setDisciplina(claseNueva.getDisciplina());
        antes.setInstructor(claseNueva.getInstructor());
        antes.setSede(claseNueva.getSede());
        antes.setFechaInicio(claseNueva.getFechaInicio());
        antes.setFechaFin(claseNueva.getFechaFin());
        antes.setCupoMaximo(claseNueva.getCupoMaximo());
        antes.setCupoActual(claseNueva.getCupoActual());

        Clase despues = claseRepository.save(antes);

        // ✔ CANCELADA → Notificación y fin
        if (cancelada) {
            notificacionService.generarNotificacionCancelada(despues);
            return despues;
        }

        // ✔ Detectar cambios relevantes → REPROGRAMADA
        boolean reprogramada =
                !original.getFechaInicio().equals(despues.getFechaInicio()) ||
                !original.getFechaFin().equals(despues.getFechaFin()) ||
                !original.getSede().getId().equals(despues.getSede().getId()) ||
                !original.getInstructor().getId().equals(despues.getInstructor().getId()) ||
                !original.getDisciplina().getId().equals(despues.getDisciplina().getId()) ||
                !original.getNombre().equals(despues.getNombre()) ||
                !original.getDescripcion().equals(despues.getDescripcion());

        if (reprogramada) {
            notificacionService.generarNotificacionReprogramada(original, despues);
        }

        return despues;
    }

    // ----------------------------------------------------------
    // ✔ DTO MAPPERS
    // ----------------------------------------------------------
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
                .activo(clase.isActivo()) // ← Asegurate que ClaseDto tiene este campo
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
