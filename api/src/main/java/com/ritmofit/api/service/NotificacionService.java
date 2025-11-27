package com.ritmofit.api.service;

import com.ritmofit.api.dto.NotificacionDto;
import com.ritmofit.api.model.entity.Clase;
import com.ritmofit.api.model.entity.Notificacion;
import com.ritmofit.api.model.entity.NotificacionMapper;
import com.ritmofit.api.model.entity.Reserva;
import com.ritmofit.api.repository.ClaseRepository;
import com.ritmofit.api.repository.NotificacionRepository;
import com.ritmofit.api.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final ClaseRepository claseRepository;
    private final ReservaRepository reservaRepository;

    public List<NotificacionDto> obtenerPendientes(Long usuarioId) {
        LocalDateTime ahora = LocalDateTime.now();
        List<Notificacion> pendientes = notificacionRepository.findPendientesConClaseCargada(usuarioId, ahora);
        pendientes.forEach(n -> n.setEnviada(true)); // solo se marca enviada si ya pasó la fecha
        notificacionRepository.saveAll(pendientes);
        return pendientes.stream().map(NotificacionMapper::toDto).toList();
    }

    @Transactional
    public void generarRecordatorios() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime dentroDeUnaHora = ahora.plusHours(1);

        List<Reserva> reservas = reservaRepository.findByClaseFechaInicioBetween(ahora, dentroDeUnaHora);

        for (Reserva reserva : reservas) {
            Clase clase = reserva.getClase();
            if (!clase.isActivo()) continue;

            Long usuarioId = reserva.getUsuario().getId();

            boolean existe = notificacionRepository.existsByClaseIdAndTipoAndUsuarioId(
                    clase.getId(), "RECORDATORIO", usuarioId);

            if (!existe) {
                Notificacion noti = Notificacion.builder()
                        .clase(clase)
                        .usuarioId(usuarioId)
                        .tipo("RECORDATORIO")
                        .mensaje("Tu clase " + clase.getNombre() + " comienza en 1 hora.")
                        .fechaEnvio(ahora)
                        .enviada(false)
                        .build();

                notificacionRepository.save(noti);
            }
        }
    }

    @Transactional
    public void generarNotificacionReprogramada(Clase antes, Clase despues) {
        if (!despues.isActivo()) return;

        List<Reserva> reservas = reservaRepository.findByClaseIdAndEstado(
                despues.getId(), Reserva.EstadoReserva.CONFIRMADA);

        for (Reserva r : reservas) {

            String mensaje = "Tu clase '" + despues.getNombre() +
                    "' fue reprogramada. Nueva fecha: " + despues.getFechaInicio();

            Notificacion n = Notificacion.builder()
                    .clase(despues)
                    .usuarioId(r.getUsuario().getId())
                    .tipo("REPROGRAMADA")
                    .mensaje(mensaje)
                    .fechaEnvio(LocalDateTime.now())
                    .enviada(false)
                    .build();

            notificacionRepository.save(n);

            actualizarRecordatorioDe1Hora(r, despues);
        }
    }

    @Transactional
    public void generarNotificacionCancelada(Clase clase) {
        List<Reserva> reservas = reservaRepository.findByClaseIdAndEstado(
                clase.getId(), Reserva.EstadoReserva.CONFIRMADA);

        for (Reserva r : reservas) {

            Notificacion n = Notificacion.builder()
                    .clase(clase)
                    .usuarioId(r.getUsuario().getId())
                    .tipo("CANCELADA")
                    .mensaje("Tu clase '" + clase.getNombre() + "' fue cancelada.")
                    .fechaEnvio(LocalDateTime.now())
                    .enviada(false)
                    .build();

            notificacionRepository.save(n);
        }
    }

    private void actualizarRecordatorioDe1Hora(Reserva reserva, Clase clase) {
        LocalDateTime nuevaFecha = clase.getFechaInicio().minusHours(1);

        Notificacion n = Notificacion.builder()
                .clase(clase)
                .usuarioId(reserva.getUsuario().getId())
                .tipo("RECORDATORIO")
                .mensaje("Tu clase " + clase.getNombre() + " comienza en 1 hora.")
                .fechaEnvio(nuevaFecha)
                .enviada(false)
                .build();

        notificacionRepository.save(n);
    }
}
