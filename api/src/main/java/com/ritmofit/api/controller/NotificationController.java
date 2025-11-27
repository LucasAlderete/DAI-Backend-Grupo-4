package com.ritmofit.api.controller;

import com.ritmofit.api.dto.NotificacionDto;
import com.ritmofit.api.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificacionService notificacionService;

    @GetMapping("/pending/{usuarioId}")
    @Operation(
            summary = "Obtener notificaciones pendientes",
            description = "Devuelve notificaciones que deben enviarse al usuario: recordatorios, reprogramaciones, cancelaciones"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    })
    public ResponseEntity<List<NotificacionDto>> obtenerNotificacionesPendientes(
            @PathVariable Long usuarioId
    ) {
        return ResponseEntity.ok(
                notificacionService.obtenerPendientes(usuarioId)
        );
    }

    @PostMapping("/generar")
        public ResponseEntity<?> generar() {
        notificacionService.generarRecordatorios();
        return ResponseEntity.ok("OK");
        }

}
