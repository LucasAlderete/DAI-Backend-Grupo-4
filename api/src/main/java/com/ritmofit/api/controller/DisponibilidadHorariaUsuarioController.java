package com.ritmofit.api.controller;

import com.ritmofit.api.dto.DisponibilidadHorariaDto;
import com.ritmofit.api.model.entity.DiaSemana;
import com.ritmofit.api.service.DisponibilidadHorariaUsuarioService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disponibilidad-horaria")
@RequiredArgsConstructor
@Tag(name = "Disponibilidad Horaria", description = "API para gestionar disponibilidad horaria del usuario")
public class DisponibilidadHorariaUsuarioController {

    private final DisponibilidadHorariaUsuarioService disponibilidadService;

    @GetMapping
    @Operation(summary = "Obtener disponibilidad horaria", description = "Retorna la lista de disponibilidades horarias del usuario autenticado")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidades encontradas exitosamente",
                    content = @Content(schema = @Schema(implementation = DisponibilidadHorariaDto.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<DisponibilidadHorariaDto>> obtenerDisponibilidades(Authentication authentication) {
        String emailUsuario = authentication.getName();
        List<DisponibilidadHorariaDto> disponibilidades = disponibilidadService.obtenerDisponibilidadesPorUsuario(emailUsuario);
        return ResponseEntity.ok(disponibilidades);
    }

    @GetMapping("/dia/{diaSemana}")
    @Operation(summary = "Obtener disponibilidad por día", description = "Retorna las disponibilidades horarias del usuario para un día específico")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidades encontradas exitosamente",
                    content = @Content(schema = @Schema(implementation = DisponibilidadHorariaDto.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<DisponibilidadHorariaDto>> obtenerDisponibilidadesPorDia(
            Authentication authentication,
            @Parameter(description = "Día de la semana", required = true)
            @PathVariable DiaSemana diaSemana) {
        String emailUsuario = authentication.getName();
        List<DisponibilidadHorariaDto> disponibilidades = disponibilidadService.obtenerDisponibilidadesPorDia(emailUsuario, diaSemana);
        return ResponseEntity.ok(disponibilidades);
    }

    @PostMapping
    @Operation(summary = "Agregar disponibilidad horaria", description = "Crea una nueva disponibilidad horaria para el usuario")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidad creada exitosamente",
                    content = @Content(schema = @Schema(implementation = DisponibilidadHorariaDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<DisponibilidadHorariaDto> agregarDisponibilidad(
            Authentication authentication,
            @Parameter(description = "Datos de la disponibilidad horaria", required = true)
            @Valid @RequestBody DisponibilidadHorariaDto dto) {
        String emailUsuario = authentication.getName();
        DisponibilidadHorariaDto nuevaDisponibilidad = disponibilidadService.agregarDisponibilidad(emailUsuario, dto);
        return ResponseEntity.ok(nuevaDisponibilidad);
    }

    @PutMapping("/{disponibilidadId}")
    @Operation(summary = "Actualizar disponibilidad horaria", description = "Actualiza una disponibilidad horaria existente")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = DisponibilidadHorariaDto.class))),
        @ApiResponse(responseCode = "404", description = "Disponibilidad no encontrada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "No tienes permisos para modificar esta disponibilidad"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<DisponibilidadHorariaDto> actualizarDisponibilidad(
            Authentication authentication,
            @Parameter(description = "ID de la disponibilidad a actualizar", required = true)
            @PathVariable Long disponibilidadId,
            @Parameter(description = "Nuevos datos de la disponibilidad", required = true)
            @Valid @RequestBody DisponibilidadHorariaDto dto) {
        String emailUsuario = authentication.getName();
        DisponibilidadHorariaDto disponibilidadActualizada = disponibilidadService.actualizarDisponibilidad(emailUsuario, disponibilidadId, dto);
        return ResponseEntity.ok(disponibilidadActualizada);
    }

    @DeleteMapping("/{disponibilidadId}")
    @Operation(summary = "Eliminar disponibilidad horaria", description = "Elimina (desactiva) una disponibilidad horaria")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidad eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Disponibilidad no encontrada"),
        @ApiResponse(responseCode = "403", description = "No tienes permisos para eliminar esta disponibilidad"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminarDisponibilidad(
            Authentication authentication,
            @Parameter(description = "ID de la disponibilidad a eliminar", required = true)
            @PathVariable Long disponibilidadId) {
        String emailUsuario = authentication.getName();
        disponibilidadService.eliminarDisponibilidad(emailUsuario, disponibilidadId);
        return ResponseEntity.ok().build();
    }
}
