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

        // Verificar que no existe ya una asistencia para esta clase
        Asistencia asistenciaExistente = asistenciaRepository.findByUsuarioAndClase(usuario, claseId);
        if (asistenciaExistente != null) {
            throw new RuntimeException("Ya existe una asistencia registrada para esta clase");
        }

        // Crear la asistencia
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

        // Verificar que la asistencia pertenece al usuario
        if (!asistencia.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permisos para calificar esta asistencia");
        }

        // Verificar que la asistencia es reciente (dentro de las últimas 24 horas)
        LocalDateTime limiteCalificacion = asistencia.getFechaAsistencia().plusHours(24);
        if (LocalDateTime.now().isAfter(limiteCalificacion)) {
            throw new RuntimeException("El tiempo para calificar esta asistencia ha expirado");
        }

        // Actualizar la calificación
        asistencia.setCalificacion(calificacionDto.getCalificacion());
        asistencia.setComentario(calificacionDto.getComentario());
        asistencia.setFechaCalificacion(LocalDateTime.now());
        
        asistencia = asistenciaRepository.save(asistencia);

        return convertirAAsistenciaDto(asistencia);
    }

    public Page<AsistenciaDto> obtenerAsistenciasCalificables(String emailUsuario, int page, int size) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LocalDateTime limiteCalificacion = LocalDateTime.now().minusHours(24);
        
        AsistenciaFilterDto filtros = new AsistenciaFilterDto();
        filtros.setFechaInicio(limiteCalificacion);
        filtros.setPage(page);
        filtros.setSize(size);

        Page<AsistenciaDto> asistencias = obtenerHistorialAsistencias(emailUsuario, filtros);
        
        // Filtrar solo las que no tienen calificación
        return asistencias.map(asistencia -> {
            if (asistencia.getCalificacion() == null) {
                return asistencia;
            }
            return null;
        });
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
                .fechaInicio(clase.getFechaInicio())
                .fechaFin(clase.getFechaFin())
                .cupoMaximo(clase.getCupoMaximo())
                .cupoActual(clase.getCupoActual())
                .disponible(clase.getCupoActual() < clase.getCupoMaximo())
                .build();
    }
}
