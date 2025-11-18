package com.ritmofit.api.controller;

import com.ritmofit.api.dto.DisciplinaUsuarioDto;
import com.ritmofit.api.service.DisciplinaUsuarioService;
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
@RequestMapping("/api/disciplinas-favoritas")
@RequiredArgsConstructor
@Tag(name = "Disciplinas Favoritas", description = "API para gestionar disciplinas favoritas del usuario")
public class DisciplinaUsuarioController {

    private final DisciplinaUsuarioService disciplinaUsuarioService;

    @GetMapping
    @Operation(summary = "Obtener disciplinas favoritas", description = "Retorna la lista de disciplinas favoritas del usuario autenticado")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disciplinas favoritas encontradas exitosamente",
                    content = @Content(schema = @Schema(implementation = DisciplinaUsuarioDto.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<DisciplinaUsuarioDto>> obtenerDisciplinasFavoritas(Authentication authentication) {
        String emailUsuario = authentication.getName();
        List<DisciplinaUsuarioDto> disciplinas = disciplinaUsuarioService.obtenerDisciplinasFavoritasPorUsuario(emailUsuario);
        return ResponseEntity.ok(disciplinas);
    }

    @PostMapping("/{disciplinaId}")
    @Operation(summary = "Agregar disciplina favorita", description = "Marca una disciplina como favorita para el usuario")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disciplina agregada como favorita exitosamente",
                    content = @Content(schema = @Schema(implementation = DisciplinaUsuarioDto.class))),
        @ApiResponse(responseCode = "404", description = "Disciplina no encontrada"),
        @ApiResponse(responseCode = "409", description = "La disciplina ya está marcada como favorita"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<DisciplinaUsuarioDto> agregarDisciplinaFavorita(
            Authentication authentication,
            @Parameter(description = "ID de la disciplina a marcar como favorita", required = true)
            @PathVariable Long disciplinaId) {
        String emailUsuario = authentication.getName();
        DisciplinaUsuarioDto disciplinaFavorita = disciplinaUsuarioService.agregarDisciplinaFavorita(emailUsuario, disciplinaId);
        return ResponseEntity.ok(disciplinaFavorita);
    }

    @DeleteMapping("/{disciplinaUsuarioId}")
    @Operation(summary = "Eliminar disciplina favorita", description = "Elimina una disciplina de la lista de favoritas del usuario")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disciplina favorita eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Disciplina favorita no encontrada"),
        @ApiResponse(responseCode = "403", description = "No tienes permisos para eliminar esta disciplina"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminarDisciplinaFavorita(
            Authentication authentication,
            @Parameter(description = "ID de la relación disciplina-usuario a eliminar", required = true)
            @PathVariable Long disciplinaUsuarioId) {
        String emailUsuario = authentication.getName();
        disciplinaUsuarioService.eliminarDisciplinaFavorita(emailUsuario, disciplinaUsuarioId);
        return ResponseEntity.ok().build();
    }
}
