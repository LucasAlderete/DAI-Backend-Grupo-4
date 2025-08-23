package com.ritmofit.api.controller;

import com.ritmofit.api.dto.ReservaDto;
import com.ritmofit.api.model.entity.Reserva;
import com.ritmofit.api.service.IReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "API para gestionar reservas de clases en el gimnasio")
public class ReservaController {
    
    @Autowired
    private IReservaService reservaService;
    
    @GetMapping
    @Operation(summary = "Obtener todas las reservas", description = "Retorna una lista de todas las reservas disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservas encontradas exitosamente",
                    content = @Content(schema = @Schema(implementation = Reserva.class))),
        @ApiResponse(responseCode = "404", description = "No se encontraron reservas"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<Reserva>> getTodasMisReservas() {
        List<Reserva> reservas = reservaService.getAll();
        return ResponseEntity.ok(reservas);
    }
    
    @PostMapping
    @Operation(summary = "Crear una nueva reserva", description = "Crea una nueva reserva para una clase en el gimnasio")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva creada exitosamente",
                    content = @Content(schema = @Schema(implementation = Reserva.class))),
        @ApiResponse(responseCode = "400", description = "Datos de reserva inválidos"),
        @ApiResponse(responseCode = "409", description = "Conflicto - La clase ya está llena o el usuario ya tiene reserva"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Reserva> crearReserva(
            @Parameter(description = "Datos de la reserva a crear", required = true)
            @RequestBody ReservaDto reservaDto) {
        Reserva nuevaReserva = reservaService.create(reservaDto);
        return ResponseEntity.ok(nuevaReserva);
    }
}
