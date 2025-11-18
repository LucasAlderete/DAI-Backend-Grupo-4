package com.ritmofit.api.service;

import com.ritmofit.api.dto.DisponibilidadHorariaDto;
import com.ritmofit.api.model.entity.DiaSemana;
import com.ritmofit.api.model.entity.DisponibilidadHorariaUsuario;
import com.ritmofit.api.model.entity.Usuario;
import com.ritmofit.api.repository.DisponibilidadHorariaUsuarioRepository;
import com.ritmofit.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DisponibilidadHorariaUsuarioService {

    private final DisponibilidadHorariaUsuarioRepository disponibilidadRepository;
    private final UsuarioRepository usuarioRepository;

    public List<DisponibilidadHorariaDto> obtenerDisponibilidadesPorUsuario(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return disponibilidadRepository.findByUsuarioAndActivoTrue(usuario).stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    public List<DisponibilidadHorariaDto> obtenerDisponibilidadesPorDia(String emailUsuario, DiaSemana diaSemana) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return disponibilidadRepository.findByUsuarioIdAndDiaSemanaAndActivoTrue(usuario.getId(), diaSemana).stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    public DisponibilidadHorariaDto agregarDisponibilidad(String emailUsuario, DisponibilidadHorariaDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar que hora inicio sea menor que hora fin
        if (dto.getHoraInicio().isAfter(dto.getHoraFin()) || dto.getHoraInicio().equals(dto.getHoraFin())) {
            throw new RuntimeException("La hora de inicio debe ser menor que la hora de fin");
        }

        DisponibilidadHorariaUsuario disponibilidad = DisponibilidadHorariaUsuario.builder()
                .usuario(usuario)
                .diaSemana(dto.getDiaSemana())
                .horaInicio(dto.getHoraInicio())
                .horaFin(dto.getHoraFin())
                .activo(true)
                .build();

        disponibilidad = disponibilidadRepository.save(disponibilidad);
        return convertirADto(disponibilidad);
    }

    public DisponibilidadHorariaDto actualizarDisponibilidad(String emailUsuario, Long disponibilidadId, DisponibilidadHorariaDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        DisponibilidadHorariaUsuario disponibilidad = disponibilidadRepository.findById(disponibilidadId)
                .orElseThrow(() -> new RuntimeException("Disponibilidad no encontrada"));

        // Verificar que pertenece al usuario
        if (!disponibilidad.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permisos para modificar esta disponibilidad");
        }

        // Validar que hora inicio sea menor que hora fin
        if (dto.getHoraInicio().isAfter(dto.getHoraFin()) || dto.getHoraInicio().equals(dto.getHoraFin())) {
            throw new RuntimeException("La hora de inicio debe ser menor que la hora de fin");
        }

        disponibilidad.setDiaSemana(dto.getDiaSemana());
        disponibilidad.setHoraInicio(dto.getHoraInicio());
        disponibilidad.setHoraFin(dto.getHoraFin());

        disponibilidad = disponibilidadRepository.save(disponibilidad);
        return convertirADto(disponibilidad);
    }

    public void eliminarDisponibilidad(String emailUsuario, Long disponibilidadId) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        DisponibilidadHorariaUsuario disponibilidad = disponibilidadRepository.findById(disponibilidadId)
                .orElseThrow(() -> new RuntimeException("Disponibilidad no encontrada"));

        // Verificar que pertenece al usuario
        if (!disponibilidad.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permisos para eliminar esta disponibilidad");
        }

        // Soft delete
        disponibilidad.setActivo(false);
        disponibilidadRepository.save(disponibilidad);
    }

    private DisponibilidadHorariaDto convertirADto(DisponibilidadHorariaUsuario disponibilidad) {
        return DisponibilidadHorariaDto.builder()
                .id(disponibilidad.getId())
                .diaSemana(disponibilidad.getDiaSemana())
                .horaInicio(disponibilidad.getHoraInicio())
                .horaFin(disponibilidad.getHoraFin())
                .build();
    }
}
