package com.ritmofit.api.service;

import com.ritmofit.api.dto.*;
import com.ritmofit.api.model.entity.*;
import com.ritmofit.api.repository.ClaseRepository;
import com.ritmofit.api.repository.ReservaRespository;
import com.ritmofit.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService implements IReservaService {

    private final ReservaRespository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClaseRepository claseRepository;

    public List<Reserva> getAll() {
        return reservaRepository.findAll();
    }

    @Override
    public Reserva create(ReservaDto nuevaReserva) {
        // Este método se mantiene por compatibilidad, pero se recomienda usar crearReserva
        throw new UnsupportedOperationException("Use crearReserva(CrearReservaDto, String) instead");
    }

    public ReservaDto crearReserva(CrearReservaDto crearReservaDto, String emailUsuario) {
        // Validar que el usuario existe
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar que la clase existe
        Clase clase = claseRepository.findById(crearReservaDto.getClaseId())
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        // Validar que la clase no ha comenzado
        if (clase.getFechaInicio().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede reservar una clase que ya ha comenzado");
        }

        // Validar que no hay cupo disponible
        Long reservasActivas = reservaRepository.countReservasActivasPorClase(clase.getId());
        if (reservasActivas >= clase.getCupoMaximo()) {
            throw new RuntimeException("No hay cupo disponible para esta clase");
        }

        // Validar que el usuario no tiene ya una reserva activa para esta clase
        Reserva reservaExistente = reservaRepository.findReservaActiva(usuario, clase.getId());
        if (reservaExistente != null) {
            throw new RuntimeException("Ya tienes una reserva activa para esta clase");
        }

        // Crear la reserva
        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .clase(clase)
                .fechaReserva(LocalDateTime.now())
                .estado(Reserva.EstadoReserva.CONFIRMADA)
                .build();

        reserva = reservaRepository.save(reserva);

        // Actualizar el cupo actual de la clase
        clase.setCupoActual(clase.getCupoActual() + 1);
        claseRepository.save(clase);

        return convertirAReservaDto(reserva);
    }

    public ReservaDto cancelarReserva(Long reservaId, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Validar que la reserva pertenece al usuario
        if (!reserva.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permisos para cancelar esta reserva");
        }

        // Validar que la reserva está confirmada
        if (reserva.getEstado() != Reserva.EstadoReserva.CONFIRMADA) {
            throw new RuntimeException("Solo se pueden cancelar reservas confirmadas");
        }

        // Validar que la clase no ha comenzado
        if (reserva.getClase().getFechaInicio().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede cancelar una reserva de una clase que ya ha comenzado");
        }

        // Cancelar la reserva
        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        reserva.setFechaCancelacion(LocalDateTime.now());
        reserva = reservaRepository.save(reserva);

        // Actualizar el cupo actual de la clase
        Clase clase = reserva.getClase();
        clase.setCupoActual(clase.getCupoActual() - 1);
        claseRepository.save(clase);

        return convertirAReservaDto(reserva);
    }

    public Page<ReservaDto> obtenerReservasUsuario(String emailUsuario, int page, int size) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Reserva> reservas = reservaRepository.findByUsuarioOrderByFechaInicioDesc(usuario, pageable);

        return reservas.map(this::convertirAReservaDto);
    }

    public List<ReservaDto> obtenerProximasReservas(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Reserva> reservas = reservaRepository.findProximasReservas(usuario, LocalDateTime.now());
        return reservas.stream()
                .map(this::convertirAReservaDto)
                .collect(Collectors.toList());
    }

    private ReservaDto convertirAReservaDto(Reserva reserva) {
        return ReservaDto.builder()
                .id(reserva.getId())
                .usuarioId(reserva.getUsuario().getId())
                .claseId(reserva.getClase().getId())
                .clase(convertirAClaseDto(reserva.getClase()))
                .fechaReserva(reserva.getFechaReserva())
                .estado(reserva.getEstado())
                .fechaCancelacion(reserva.getFechaCancelacion())
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
