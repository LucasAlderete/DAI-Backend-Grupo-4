package com.ritmofit.api.service;

import com.ritmofit.api.dto.*;
import com.ritmofit.api.model.entity.*;
import com.ritmofit.api.repository.AsistenciaRepository;
import com.ritmofit.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public Page<AsistenciaDto> obtenerHistorialAsistencias(String emailUsuario, AsistenciaFilterDto filtros) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Pageable pageable = PageRequest.of(filtros.getPage(), filtros.getSize());
        
        Page<Asistencia> asistencias = asistenciaRepository.findAsistenciasConFiltros(
                usuario,
                filtros.getFechaInicio(),
                filtros.getFechaFin(),
                pageable);
        
        return asistencias.map(this::convertirAAsistenciaDto);
    }

    public AsistenciaDto registrarAsistencia(String emailUsuario, Long claseId, Long reservaId) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Asistencia asistenciaExistente = asistenciaRepository.findByUsuarioAndClase(usuario, claseId);
        if (asistenciaExistente != null) {
            throw new RuntimeException("Ya existe una asistencia registrada para esta clase");
        }

        Asistencia asistencia = Asistencia.builder()
                .usuario(usuario)
                .clase(Clase.builder().id(claseId).build())
                .reserva(Reserva.builder().id(reservaId).build())
                .fechaAsistencia(LocalDateTime.now())
                .fechaCheckin(LocalDateTime.now())
                .build();

        asistencia = asistenciaRepository.save(asistencia);

        return convertirAAsistenciaDto(asistencia);
    }

    public AsistenciaDto calificarAsistencia(String emailUsuario, Long asistenciaId, CalificarAsistenciaDto calificacionDto) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Asistencia asistencia = asistenciaRepository.findById(asistenciaId)
                .orElseThrow(() -> new RuntimeException("Asistencia no encontrada"));

        if (!asistencia.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permisos para calificar esta asistencia");
        }

        // Validar que no se haya calificado previamente
        if (asistencia.getCalificacion() != null) {
            throw new RuntimeException("Esta asistencia ya ha sido calificada");
        }

        // Validar que esté dentro de las 24 horas desde el check-in
        LocalDateTime limiteCalificacion = asistencia.getFechaCheckin().plusHours(24);
        if (LocalDateTime.now().isAfter(limiteCalificacion)) {
            throw new RuntimeException("El tiempo para calificar esta asistencia ha expirado (debe ser dentro de las 24 horas posteriores al check-in)");
        }

        asistencia.setCalificacion(calificacionDto.getCalificacion());
        asistencia.setComentario(calificacionDto.getComentario());
        asistencia.setFechaCalificacion(LocalDateTime.now());
        
        asistencia = asistenciaRepository.save(asistencia);

        return convertirAAsistenciaDto(asistencia);
    }

    public Page<AsistenciaDto> obtenerAsistenciasCalificables(String emailUsuario, int page, int size) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LocalDateTime ahora = LocalDateTime.now();
        // Calcular la fecha mínima: hace 24 horas desde ahora
        LocalDateTime fechaMinima = ahora.minusHours(24);
        // La fecha máxima es ahora (solo asistencias que aún pueden calificarse)
        LocalDateTime fechaMaxima = ahora;
        
        Pageable pageable = PageRequest.of(page, size);
        
        // Usar el método del repositorio que filtra por fechaCheckin dentro de las últimas 24 horas y sin calificación
        Page<Asistencia> asistencias = asistenciaRepository.findAsistenciasCalificables(
                usuario, 
                fechaMinima,
                fechaMaxima,
                pageable);
        
        // Convertir a DTO (la consulta ya filtra correctamente, así que todas deberían ser válidas)
        return asistencias.map(this::convertirAAsistenciaDto);
    }

    private AsistenciaDto convertirAAsistenciaDto(Asistencia asistencia) {
        return AsistenciaDto.builder()
                .id(asistencia.getId())
                .usuarioId(asistencia.getUsuario().getId())
                .claseId(asistencia.getClase().getId())
                .clase(convertirAClaseDto(asistencia.getClase()))
                .fechaAsistencia(asistencia.getFechaAsistencia())
                .fechaCheckin(asistencia.getFechaCheckin())
                .duracionMinutos(asistencia.getDuracionMinutos())
                .calificacion(asistencia.getCalificacion())
                .comentario(asistencia.getComentario())
                .fechaCalificacion(asistencia.getFechaCalificacion())
                .build();
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

    private SedeDto convertirASedeDto(Sede sede) {
        if (sede == null) return null;
        return SedeDto.builder()
                .id(sede.getId())
                .nombre(sede.getNombre())
                .direccion(sede.getDireccion())
                .telefono(sede.getTelefono())
                .build();
    }

    private DisciplinaDto convertirADisciplinaDto(Disciplina disciplina) {
        if (disciplina == null) return null;
        return DisciplinaDto.builder()
                .id(disciplina.getId())
                .nombre(disciplina.getNombre())
                .descripcion(disciplina.getDescripcion())
                .build();
    }

    private InstructorDto convertirAInstructorDto(Instructor instructor) {
        if (instructor == null) return null;
        return InstructorDto.builder()
                .id(instructor.getId())
                .nombre(instructor.getNombre())
                .apellido(instructor.getApellido())
                .email(instructor.getEmail())
                .telefono(instructor.getTelefono())
                .build();
    }
}
