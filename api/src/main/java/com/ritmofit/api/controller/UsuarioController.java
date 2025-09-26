package com.ritmofit.api.controller;

import com.ritmofit.api.dto.UsuarioDto;
import com.ritmofit.api.service.UsuarioService;
import com.ritmofit.api.service.AuthService;
import com.ritmofit.api.dto.AuthResponseDto;
import com.ritmofit.api.dto.AuthRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
            @Valid @RequestBody UsuarioDto usuarioDto) {
        String email = authentication.getName();
        UsuarioDto perfilActualizado = usuarioService.actualizarPerfil(email, usuarioDto);
        return ResponseEntity.ok(perfilActualizado);
    }

    // ======================================
    // REGISTER
    // ======================================
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

    // ======================================
    // LOGIN
    // ======================================
    @PostMapping("/login")
    @Operation(summary = "Login usuario", description = "Inicia sesión con email usando OTP")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso",
                content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Código OTP inválido o expirado")
    })
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto authRequest) {
        AuthResponseDto response = authService.solicitarCodigo(authRequest);
        return ResponseEntity.ok(response);
    }
}
