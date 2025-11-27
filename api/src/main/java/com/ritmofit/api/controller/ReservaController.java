package com.ritmofit.api.controller;

import com.ritmofit.api.dto.*;
import com.ritmofit.api.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "API para gestionar reservas de clases en el gimnasio")
public class ReservaController {
    
    private final ReservaService reservaService;
    
    @PostMapping
    @Operation(summary = "Crear una nueva reserva", description = "Crea una nueva reserva para una clase en el gimnasio")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva creada exitosamente",
                    content = @Content(schema = @Schema(implementation = ReservaDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de reserva inválidos"),
        @ApiResponse(responseCode = "409", description = "Conflicto - La clase ya está llena o el usuario ya tiene reserva"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ReservaDto> crearReserva(
            Authentication authentication,
            @Parameter(description = "Datos de la reserva a crear", required = true)
            @Valid @RequestBody CrearReservaDto crearReservaDto) {
        String emailUsuario = authentication.getName();
        ReservaDto nuevaReserva = reservaService.crearReserva(crearReservaDto, emailUsuario);
        return ResponseEntity.ok(nuevaReserva);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar una reserva", description = "Cancela una reserva existente")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente",
                    content = @Content(schema = @Schema(implementation = ReservaDto.class))),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
        @ApiResponse(responseCode = "400", description = "No se puede cancelar la reserva"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ReservaDto> cancelarReserva(
            Authentication authentication,
            @PathVariable Long id) {
        String emailUsuario = authentication.getName();
        ReservaDto reservaCancelada = reservaService.cancelarReserva(id, emailUsuario);
        return ResponseEntity.ok(reservaCancelada);
    }
    
    @GetMapping
    @Operation(summary = "Obtener mis reservas", description = "Retorna una lista paginada de las reservas del usuario autenticado")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservas encontradas exitosamente",
                    content = @Content(schema = @Schema(implementation = ReservaDto.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Page<ReservaDto>> obtenerMisReservas(
            Authentication authentication,
            @Parameter(description = "Número de página (base 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {
        String emailUsuario = authentication.getName();
        Page<ReservaDto> reservas = reservaService.obtenerReservasUsuario(emailUsuario, page, size);
        return ResponseEntity.ok(reservas);
    }
    
    @GetMapping("/proximas")
    @Operation(summary = "Obtener próximas reservas", description = "Retorna las próximas reservas confirmadas del usuario")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservas encontradas exitosamente",
                    content = @Content(schema = @Schema(implementation = ReservaDto.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<ReservaDto>> obtenerProximasReservas(Authentication authentication) {
        String emailUsuario = authentication.getName();
        List<ReservaDto> reservas = reservaService.obtenerProximasReservas(emailUsuario);
        return ResponseEntity.ok(reservas);
    }

    
    @GetMapping("/clase/{claseId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ReservaDto> obtenerReservaPorClase(
            Authentication authentication,
            @PathVariable Long claseId) {

        String emailUsuario = authentication.getName();
      

        ReservaDto reserva = reservaService.obtenerReservaPorClase(emailUsuario, claseId);
        return ResponseEntity.ok(reserva);
    }

}
