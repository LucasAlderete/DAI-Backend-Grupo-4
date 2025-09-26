package com.ritmofit.api.controller;

import com.ritmofit.api.dto.*;
import com.ritmofit.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "API para login con contraseña y verificación de email con OTP")
public class AuthController {

    private final AuthService authService;

    // ======================================
    // LOGIN (email + password, sin OTP)
    // ======================================
    @PostMapping("/login")
    @Operation(summary = "Login con contraseña", description = "Inicia sesión con email y contraseña")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso",
                content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Credenciales inválidas o email no verificado")
    })
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(400).body(
                    AuthResponseDto.builder().mensaje(ex.getMessage()).build()
            );
        }
    }

    // ======================================
    // REGISTER (con OTP para verificar email)
    // ======================================
    @PostMapping("/register")
    @Operation(summary = "Registro de usuario", description = "Registra un usuario con email y contraseña, y envía OTP al correo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado, OTP enviado",
                content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Email ya registrado")
    })
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(400).body(
                    AuthResponseDto.builder().mensaje(ex.getMessage()).build()
            );
        }
    }

    // ======================================
    // VERIFICAR CÓDIGO (activar email)
    // ======================================
    @PostMapping("/verificar-codigo")
    @Operation(summary = "Verificar código OTP", description = "Verifica el código OTP y activa el email del usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email verificado",
                content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Código inválido o expirado")
    })
    public ResponseEntity<AuthResponseDto> verificarCodigo(@Valid @RequestBody VerifyOtpDto request) {
        try {
            return ResponseEntity.ok(authService.verificarCodigo(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(400).body(
                    AuthResponseDto.builder().mensaje(ex.getMessage()).build()
            );
        }
    }

    // ======================================
    // REENVIAR CÓDIGO
    // ======================================
    @PostMapping("/reenviar-codigo")
    @Operation(summary = "Reenviar código OTP", description = "Reenvía un nuevo OTP para verificación de email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OTP reenviado exitosamente",
                content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Usuario no encontrado")
    })
    public ResponseEntity<AuthResponseDto> reenviarCodigo(@Valid @RequestBody AuthRequestDto request) {
        try {
            return ResponseEntity.ok(authService.reenviarCodigo(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(400).body(
                    AuthResponseDto.builder().mensaje(ex.getMessage()).build()
            );
        }
    }
}
