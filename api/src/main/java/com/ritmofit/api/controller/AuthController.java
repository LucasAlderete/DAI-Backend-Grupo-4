package com.ritmofit.api.controller;

import java.util.Map;
import com.ritmofit.api.dto.*;
import com.ritmofit.api.service.AuthService;
import com.ritmofit.api.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "API para login con contraseña y verificación de email con OTP")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

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

    // ======================================
    // VALIDAR TOKEN JWT
    // ======================================
    @GetMapping("/validate")
    @Operation(summary = "Validar token JWT", description = "Verifica si el token JWT es válido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token válido",
                content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        try {
            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(
                    java.util.Map.of("valid", false, "error", "Token not provided")
                );
            }

            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);

            if (userEmail == null) {
                return ResponseEntity.status(401).body(
                    java.util.Map.of("valid", false, "error", "Invalid token format")
                );
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                return ResponseEntity.ok(
                    java.util.Map.of("valid", true, "email", userEmail)
                );
            } else {
                return ResponseEntity.status(401).body(
                    java.util.Map.of("valid", false, "error", "Token expired")
                );
            }

        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                java.util.Map.of("valid", false, "error", "Token expired")
            );
        }
    }

    // ======================================
    // ENVIAR OTP SIN PASSWORD (solo email)
    // ======================================
    @PostMapping("/enviar-otp")
    @Operation(summary = "Enviar OTP sin autenticación", description = "Envía un nuevo OTP a un email registrado sin requerir contraseña")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OTP enviado exitosamente",
                content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Email no encontrado o error en el envío")
    })
    public ResponseEntity<AuthResponseDto> enviarOtpSinPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(AuthResponseDto.builder().mensaje("Debe ingresar un email").build());
            }

            return ResponseEntity.ok(authService.enviarOtpSinPassword(email));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(400).body(
                    AuthResponseDto.builder().mensaje(ex.getMessage()).build()
            );
        }
    }

    @PostMapping("/reenviar-codigo-email")
public ResponseEntity<AuthResponseDto> reenviarCodigoPorEmail(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    if (email == null || email.isEmpty()) {
        throw new RuntimeException("El email es obligatorio");
    }

    AuthResponseDto response = authService.enviarOtpSinPassword(email);
    return ResponseEntity.ok(response);
}


}
