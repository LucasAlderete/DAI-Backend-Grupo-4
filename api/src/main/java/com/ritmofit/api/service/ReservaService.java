package com.ritmofit.api.service;

import com.ritmofit.api.dto.*;
import com.ritmofit.api.model.entity.*;
import com.ritmofit.api.repository.ClaseRepository;
import com.ritmofit.api.repository.ReservaRepository;
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

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClaseRepository claseRepository;

    public List<Reserva> getAll() {
        return reservaRepository.findAll();
    }

    @Override
    public Reserva create(ReservaDto nuevaReserva) {
        throw new UnsupportedOperationException("Use crearReserva(CrearReservaDto, String) instead");
    }

    public ReservaDto crearReserva(CrearReservaDto crearReservaDto, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Clase clase = claseRepository.findById(crearReservaDto.getClaseId())
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        if (clase.getFechaInicio().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede reservar una clase que ya ha comenzado");
        }

        Long reservasActivas = reservaRepository.countReservasActivasPorClase(clase.getId());
        if (reservasActivas >= clase.getCupoMaximo()) {
            throw new RuntimeException("No hay cupo disponible para esta clase");
        }

        Reserva reservaExistente = reservaRepository.findReservaActiva(usuario, clase.getId());
        if (reservaExistente != null) {
            throw new RuntimeException("Ya tienes una reserva activa para esta clase");
        }

        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .clase(clase)
                .fechaReserva(LocalDateTime.now())
                .estado(Reserva.EstadoReserva.CONFIRMADA)
                .build();

        reserva = reservaRepository.save(reserva);

        clase.setCupoActual(clase.getCupoActual() + 1);
        claseRepository.save(clase);

        return convertirAReservaDto(reserva);
    }

    public ReservaDto cancelarReserva(Long reservaId, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (!reserva.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permisos para cancelar esta reserva");
        }

        if (reserva.getEstado() != Reserva.EstadoReserva.CONFIRMADA) {
            throw new RuntimeException("Solo se pueden cancelar reservas confirmadas");
        }

        if (reserva.getClase().getFechaInicio().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede cancelar una reserva de una clase que ya ha comenzado");
        }

        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        reserva.setFechaCancelacion(LocalDateTime.now());
        reserva = reservaRepository.save(reserva);

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

    public ReservaDto obtenerReservaPorClase(String emailUsuario, Long claseId) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Reserva reserva = reservaRepository.findByUsuarioIdAndClaseId(usuario.getId(), claseId);

        if (reserva == null) {
            throw new RuntimeException("No tienes reserva para esta clase");
        }

        return convertirAReservaDto(reserva);
    }
}
