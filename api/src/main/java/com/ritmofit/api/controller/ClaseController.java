package com.ritmofit.api.controller;

import com.ritmofit.api.dto.*;
import com.ritmofit.api.model.entity.Clase;
import com.ritmofit.api.service.ClaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clases")
@RequiredArgsConstructor
@Tag(name = "Catálogo de Clases", description = "API para consultar el catálogo de clases disponibles")
public class ClaseController {

    private final ClaseService claseService;

    @GetMapping
    @Operation(summary = "Obtener clases con filtros", description = "Obtiene una lista paginada de clases con filtros opcionales")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Clases obtenidas exitosamente",
                content = @Content(schema = @Schema(implementation = ClaseDto.class))),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Page<ClaseDto>> obtenerClasesConFiltros(
            @RequestParam(required = false) Long sedeId,
            @RequestParam(required = false) Long disciplinaId,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        ClaseFilterDto filtros = new ClaseFilterDto();
        filtros.setSedeId(sedeId);
        filtros.setDisciplinaId(disciplinaId);
        filtros.setPage(page);
        filtros.setSize(size);

        Page<ClaseDto> clases = claseService.obtenerClasesConFiltros(filtros);
        return ResponseEntity.ok(clases);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener clase por ID", description = "Obtiene los detalles de una clase específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Clase encontrada exitosamente",
                content = @Content(schema = @Schema(implementation = ClaseDto.class))),
        @ApiResponse(responseCode = "404", description = "Clase no encontrada")
    })
    public ResponseEntity<ClaseDto> obtenerClasePorId(@PathVariable Long id) {
        return ResponseEntity.ok(claseService.obtenerClasePorId(id));
    }

    @GetMapping("/disciplinas")
    public ResponseEntity<List<DisciplinaDto>> obtenerDisciplinas() {
        return ResponseEntity.ok(claseService.obtenerDisciplinas());
    }

    @GetMapping("/sedes")
    public ResponseEntity<List<SedeDto>> obtenerSedes() {
        return ResponseEntity.ok(claseService.obtenerSedes());
    }

    @GetMapping("/instructores")
    public ResponseEntity<List<InstructorDto>> obtenerInstructores() {
        return ResponseEntity.ok(claseService.obtenerInstructores());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar clase", description = "Actualiza una clase y genera notificación si fue reprogramada")
    public ResponseEntity<Clase> actualizarClase(
            @PathVariable Long id,
            @RequestBody Clase claseNueva) {

        return ResponseEntity.ok(claseService.actualizarClase(id, claseNueva));
    }
}
