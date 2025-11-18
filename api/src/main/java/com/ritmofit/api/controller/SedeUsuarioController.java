package com.ritmofit.api.controller;

import com.ritmofit.api.dto.SedeUsuarioDto;
import com.ritmofit.api.service.SedeUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sedes-favoritas")
@RequiredArgsConstructor
@Tag(name = "Sedes Favoritas", description = "API para gestionar sedes favoritas del usuario")
public class SedeUsuarioController {

    private final SedeUsuarioService sedeUsuarioService;

    @GetMapping
    @Operation(summary = "Obtener sedes favoritas", description = "Retorna la lista de sedes favoritas del usuario autenticado")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sedes favoritas encontradas exitosamente",
                    content = @Content(schema = @Schema(implementation = SedeUsuarioDto.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<SedeUsuarioDto>> obtenerSedesFavoritas(Authentication authentication) {
        String emailUsuario = authentication.getName();
        List<SedeUsuarioDto> sedes = sedeUsuarioService.obtenerSedesFavoritasPorUsuario(emailUsuario);
        return ResponseEntity.ok(sedes);
    }

    @PostMapping("/{sedeId}")
    @Operation(summary = "Agregar sede favorita", description = "Marca una sede como favorita para el usuario")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sede agregada como favorita exitosamente",
                    content = @Content(schema = @Schema(implementation = SedeUsuarioDto.class))),
        @ApiResponse(responseCode = "404", description = "Sede no encontrada"),
        @ApiResponse(responseCode = "409", description = "La sede ya está marcada como favorita"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<SedeUsuarioDto> agregarSedeFavorita(
            Authentication authentication,
            @Parameter(description = "ID de la sede a marcar como favorita", required = true)
            @PathVariable Long sedeId) {
        String emailUsuario = authentication.getName();
        SedeUsuarioDto sedeFavorita = sedeUsuarioService.agregarSedeFavorita(emailUsuario, sedeId);
        return ResponseEntity.ok(sedeFavorita);
    }

    @DeleteMapping("/{sedeUsuarioId}")
    @Operation(summary = "Eliminar sede favorita", description = "Elimina una sede de la lista de favoritas del usuario")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sede favorita eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Sede favorita no encontrada"),
        @ApiResponse(responseCode = "403", description = "No tienes permisos para eliminar esta sede"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminarSedeFavorita(
            Authentication authentication,
            @Parameter(description = "ID de la relación sede-usuario a eliminar", required = true)
            @PathVariable Long sedeUsuarioId) {
        String emailUsuario = authentication.getName();
        sedeUsuarioService.eliminarSedeFavorita(emailUsuario, sedeUsuarioId);
        return ResponseEntity.ok().build();
    }
}
