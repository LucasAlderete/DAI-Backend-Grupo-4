package com.ritmofit.api.controller;

import com.ritmofit.api.dto.*;
import com.ritmofit.api.service.AsistenciaService;
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

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
@Tag(name = "Historial de Asistencias", description = "API para gestionar el historial de asistencias a clases")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    @GetMapping("/historial")
    @Operation(summary = "Obtener historial de asistencias", description = "Obtiene el historial de asistencias del usuario con filtros opcionales")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = AsistenciaDto.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Page<AsistenciaDto>> obtenerHistorialAsistencias(
            Authentication authentication,
            @Parameter(description = "Fecha de inicio (YYYY-MM-DDTHH:mm:ss)") @RequestParam(required = false) String fechaInicio,
            @Parameter(description = "Fecha de fin (YYYY-MM-DDTHH:mm:ss)") @RequestParam(required = false) String fechaFin,
            @Parameter(description = "Número de página (base 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {
        
        String emailUsuario = authentication.getName();
        
        AsistenciaFilterDto filtros = new AsistenciaFilterDto();
        // TODO: Parsear fechas si se proporcionan
        filtros.setPage(page);
        filtros.setSize(size);
        
        Page<AsistenciaDto> historial = asistenciaService.obtenerHistorialAsistencias(emailUsuario, filtros);
        return ResponseEntity.ok(historial);
    }

    @PostMapping("/registrar")
    @Operation(summary = "Registrar asistencia", description = "Registra la asistencia de un usuario a una clase")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Asistencia registrada exitosamente",
                    content = @Content(schema = @Schema(implementation = AsistenciaDto.class))),
        @ApiResponse(responseCode = "400", description = "No se puede registrar la asistencia"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AsistenciaDto> registrarAsistencia(
            Authentication authentication,
            @Parameter(description = "ID de la clase") @RequestParam Long claseId,
            @Parameter(description = "ID de la reserva (opcional)") @RequestParam(required = false) Long reservaId) {
        
        String emailUsuario = authentication.getName();
        AsistenciaDto asistencia = asistenciaService.registrarAsistencia(emailUsuario, claseId, reservaId);
        return ResponseEntity.ok(asistencia);
    }

    @PostMapping("/{id}/calificar")
    @Operation(summary = "Calificar asistencia", description = "Califica una asistencia con estrellas y comentario opcional")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Asistencia calificada exitosamente",
                    content = @Content(schema = @Schema(implementation = AsistenciaDto.class))),
        @ApiResponse(responseCode = "400", description = "No se puede calificar la asistencia"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Asistencia no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AsistenciaDto> calificarAsistencia(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody CalificarAsistenciaDto calificacionDto) {
        
        String emailUsuario = authentication.getName();
        AsistenciaDto asistencia = asistenciaService.calificarAsistencia(emailUsuario, id, calificacionDto);
        return ResponseEntity.ok(asistencia);
    }

    @GetMapping("/calificables")
    @Operation(summary = "Obtener asistencias calificables", description = "Obtiene las asistencias que pueden ser calificadas (últimas 24 horas)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Asistencias obtenidas exitosamente",
                    content = @Content(schema = @Schema(implementation = AsistenciaDto.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Page<AsistenciaDto>> obtenerAsistenciasCalificables(
            Authentication authentication,
            @Parameter(description = "Número de página (base 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {
        
        String emailUsuario = authentication.getName();
        Page<AsistenciaDto> asistencias = asistenciaService.obtenerAsistenciasCalificables(emailUsuario, page, size);
        return ResponseEntity.ok(asistencias);
    }
}
