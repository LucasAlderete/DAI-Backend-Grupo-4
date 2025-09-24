package com.ritmofit.api.controller;

import com.ritmofit.api.dto.*;
import com.ritmofit.api.service.ClaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clases obtenidas exitosamente",
                    content = @Content(schema = @Schema(implementation = ClaseDto.class))),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Page<ClaseDto>> obtenerClasesConFiltros(
            @Parameter(description = "ID de la sede") @RequestParam(required = false) Long sedeId,
            @Parameter(description = "ID de la disciplina") @RequestParam(required = false) Long disciplinaId,
            @Parameter(description = "Fecha de inicio (YYYY-MM-DDTHH:mm:ss)") @RequestParam(required = false) String fechaInicio,
            @Parameter(description = "Fecha de fin (YYYY-MM-DDTHH:mm:ss)") @RequestParam(required = false) String fechaFin,
            @Parameter(description = "Número de página (base 0)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") Integer size) {
        
        ClaseFilterDto filtros = new ClaseFilterDto();
        filtros.setSedeId(sedeId);
        filtros.setDisciplinaId(disciplinaId);
        // TODO: Parsear fechas si se proporcionan
        filtros.setPage(page);
        filtros.setSize(size);
        
        Page<ClaseDto> clases = claseService.obtenerClasesConFiltros(filtros);
        return ResponseEntity.ok(clases);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener clase por ID", description = "Obtiene los detalles de una clase específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clase encontrada exitosamente",
                    content = @Content(schema = @Schema(implementation = ClaseDto.class))),
        @ApiResponse(responseCode = "404", description = "Clase no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ClaseDto> obtenerClasePorId(@PathVariable Long id) {
        ClaseDto clase = claseService.obtenerClasePorId(id);
        return ResponseEntity.ok(clase);
    }

    @GetMapping("/disciplinas")
    @Operation(summary = "Obtener disciplinas", description = "Obtiene la lista de todas las disciplinas activas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disciplinas obtenidas exitosamente",
                    content = @Content(schema = @Schema(implementation = DisciplinaDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<DisciplinaDto>> obtenerDisciplinas() {
        List<DisciplinaDto> disciplinas = claseService.obtenerDisciplinas();
        return ResponseEntity.ok(disciplinas);
    }

    @GetMapping("/sedes")
    @Operation(summary = "Obtener sedes", description = "Obtiene la lista de todas las sedes activas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sedes obtenidas exitosamente",
                    content = @Content(schema = @Schema(implementation = SedeDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<SedeDto>> obtenerSedes() {
        List<SedeDto> sedes = claseService.obtenerSedes();
        return ResponseEntity.ok(sedes);
    }

    @GetMapping("/instructores")
    @Operation(summary = "Obtener instructores", description = "Obtiene la lista de todos los instructores activos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Instructores obtenidos exitosamente",
                    content = @Content(schema = @Schema(implementation = InstructorDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<InstructorDto>> obtenerInstructores() {
        List<InstructorDto> instructores = claseService.obtenerInstructores();
        return ResponseEntity.ok(instructores);
    }
}
