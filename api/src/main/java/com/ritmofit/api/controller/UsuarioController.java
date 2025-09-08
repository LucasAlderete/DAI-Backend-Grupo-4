package com.ritmofit.api.controller;

import com.ritmofit.api.dto.UsuarioDto;
import com.ritmofit.api.model.entity.Usuario;
import com.ritmofit.api.service.IReservaService;
import com.ritmofit.api.service.IUsuarioService;
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

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "API para gestionar usuarios y perfiles")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    // obtenerUsuario
    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por id", description = "Retorna los datos de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Usuario> getUsuario(
            @Parameter(description = "Id del usuario", required = true)
            @PathVariable Long id) {
        return usuarioService.obtenerUsuario(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    // crearUsuario
    @PostMapping
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Usuario> crearUsuario(
            @Parameter(description = "Datos del usuario a crear", required = true)
            @RequestBody UsuarioDto usuarioDto) {
        Usuario nuevoUsuario = usuarioService.crearUsuario(usuarioDto);
        return ResponseEntity.ok(nuevoUsuario);
    }

    // actualizarUsuario
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Usuario> actualizarUsuario(
            @Parameter(description = "Id del usuario", required = true) @PathVariable Long id,
            @RequestBody UsuarioDto usuarioDto) {
        Usuario usuario = usuarioService.actualizarUsuario(id, usuarioDto);
        return ResponseEntity.ok(usuario);
    }

}
