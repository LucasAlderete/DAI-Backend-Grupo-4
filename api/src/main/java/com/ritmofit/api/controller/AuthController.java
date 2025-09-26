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
@Tag(name = "Autenticación", description = "API para autenticación de usuarios con OTP")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/solicitar-codigo")
    @Operation(summary = "Solicitar código OTP", description = "Envía un código de verificación al email del usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Código enviado exitosamente",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AuthResponseDto> solicitarCodigo(@Valid @RequestBody AuthRequestDto request) {
        try {
            AuthResponseDto response = authService.solicitarCodigo(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            AuthResponseDto response = AuthResponseDto.builder()
                    .mensaje(ex.getMessage())
                    .build();
            return ResponseEntity.status(400).body(response);
        }
    }

    @PostMapping("/verificar-codigo")
    @Operation(summary = "Verificar código OTP", description = "Verifica el código OTP y autentica al usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Código inválido o expirado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AuthResponseDto> verificarCodigo(@Valid @RequestBody VerifyOtpDto request) {
        try {
            AuthResponseDto response = authService.verificarCodigo(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            AuthResponseDto response = AuthResponseDto.builder()
                    .mensaje(ex.getMessage()) // resumido: "Código inválido o expirado"
                    .build();
            return ResponseEntity.status(400).body(response);
        }
    }

    @PostMapping("/reenviar-codigo")
    @Operation(summary = "Reenviar código OTP", description = "Reenvía un nuevo código de verificación al email del usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Código reenviado exitosamente",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AuthResponseDto> reenviarCodigo(@Valid @RequestBody AuthRequestDto request) {
        try {
            AuthResponseDto response = authService.reenviarCodigo(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            AuthResponseDto response = AuthResponseDto.builder()
                    .mensaje(ex.getMessage())
                    .build();
            return ResponseEntity.status(400).body(response);
        }
    }
}
