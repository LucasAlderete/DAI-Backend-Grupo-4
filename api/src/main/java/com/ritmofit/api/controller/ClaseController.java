package com.ritmofit.api.controller;

import com.ritmofit.api.dto.ClaseDTO;
import com.ritmofit.api.dto.ClaseDetalleDTO;
import com.ritmofit.api.service.IClaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/clases")
@Tag(name = "Clases", description = "API para gestionar el catálogo de clases del gimnasio")
public class ClaseController {

    @Autowired
    private IClaseService claseService;

    @GetMapping
    @Operation(summary = "Obtener listado de clases", description = "Retorna una lista paginada de clases, con opción de filtrar por sede, disciplina y fecha.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de clases obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de solicitud inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Page<ClaseDTO>> getClases(
            @Parameter(description = "ID de la sede para filtrar las clases") @RequestParam(required = false) Long sedeId,
            @Parameter(description = "ID de la disciplina para filtrar las clases") @RequestParam(required = false) Long disciplinaId,
            @Parameter(description = "Fecha para filtrar las clases (formato ISO: yyyy-MM-dd')") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @Parameter(description = "Paginación y orden de los resultados") Pageable pageable) {
        Page<ClaseDTO> clases = claseService.getClases(sedeId, disciplinaId, fecha, pageable);
        return ResponseEntity.ok(clases);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de una clase", description = "Retorna los detalles completos de una clase específica por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle de la clase obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = ClaseDetalleDTO.class))),
            @ApiResponse(responseCode = "404", description = "Clase no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ClaseDetalleDTO> getClaseById(
            @Parameter(description = "ID de la clase a obtener", required = true) @PathVariable Long id) {
        ClaseDetalleDTO clase = claseService.getClaseById(id);
        return ResponseEntity.ok(clase);
    }
}
