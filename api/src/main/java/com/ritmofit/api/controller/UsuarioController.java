package com.ritmofit.api.controller;

import com.ritmofit.api.dto.UsuarioDto;
import com.ritmofit.api.service.UsuarioService;
import com.ritmofit.api.service.AuthService;
import com.ritmofit.api.dto.AuthResponseDto;
import com.ritmofit.api.dto.AuthRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ritmofit.api.dto.UsuarioDtoUpdate;

@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
@Tag(name = "Usuario", description = "API para gestión del perfil de usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthService authService;

    @GetMapping("/perfil")
    @Operation(summary = "Obtener perfil", description = "Obtiene el perfil del usuario autenticado")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UsuarioDto> obtenerPerfil(Authentication authentication) {
        String email = authentication.getName();
        UsuarioDto perfil = usuarioService.obtenerPerfil(email);
        return ResponseEntity.ok(perfil);
    }

    @PutMapping("/perfil")
    @Operation(summary = "Actualizar perfil", description = "Actualiza el perfil del usuario autenticado")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UsuarioDto> actualizarPerfil(
            Authentication authentication,
            @Valid @RequestBody UsuarioDtoUpdate usuarioDtoUpdate) {
        String email = authentication.getName();
        UsuarioDto perfilActualizado = usuarioService.actualizarPerfil(email, usuarioDtoUpdate);
        return ResponseEntity.ok(perfilActualizado);
    }

    @PutMapping(value = "/perfil/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Actualizar imagen de perfil", description = "Permite subir o actualizar la imagen de perfil del usuario")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = UsuarioDto.class))),
            @ApiResponse(responseCode = "400", description = "Archivo inválido"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<UsuarioDto> actualizarImagenPerfil(
            Authentication authentication,
            @RequestParam("imagen") MultipartFile imagen) {
        String email = authentication.getName();
        usuarioService.actualizarImagenPerfil(email, imagen);
        UsuarioDto usuarioActualizado = usuarioService.obtenerPerfil(email);
        return ResponseEntity.ok(usuarioActualizado);
    }

    // Register
    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en la aplicación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente",
                content = @Content(schema = @Schema(implementation = UsuarioDto.class))),
        @ApiResponse(responseCode = "400", description = "Usuario ya existe o datos inválidos")
    })
    public ResponseEntity<UsuarioDto> register(@Valid @RequestBody UsuarioDto usuarioDto) {
        UsuarioDto nuevoUsuario = usuarioService.registrarUsuario(usuarioDto);
        return ResponseEntity.ok(nuevoUsuario);
    }

    // Login
    @PostMapping("/login")
    @Operation(summary = "Login usuario", description = "Inicia sesión con email usando OTP")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso",
                content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Código OTP inválido o expirado")
    })
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto authRequest) {
        AuthResponseDto response = authService.reenviarCodigo(authRequest);
        return ResponseEntity.ok(response);
    }
}
