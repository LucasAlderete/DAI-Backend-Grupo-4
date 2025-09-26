package com.ritmofit.api.service;

import com.ritmofit.api.dto.*;
import com.ritmofit.api.model.entity.OtpToken;
import com.ritmofit.api.model.entity.Usuario;
import com.ritmofit.api.repository.OtpTokenRepository;
import com.ritmofit.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder; // si en un futuro necesitás passwords
    private final CustomUserDetailsService customUserDetailsService;

    private static final SecureRandom random = new SecureRandom();

    public AuthResponseDto solicitarCodigo(AuthRequestDto request) {
        String email = request.getEmail().toLowerCase();

        // Generar código OTP de 6 dígitos
        String codigo = String.format("%06d", random.nextInt(1_000_000));

        // Crear token OTP
        OtpToken otpToken = OtpToken.builder()
                .email(email)
                .codigo(codigo)
                .fechaCreacion(LocalDateTime.now())
                .fechaExpiracion(LocalDateTime.now().plusMinutes(10))
                .usado(false)
                .tipo(OtpToken.TipoOtp.LOGIN)
                .build();

        otpTokenRepository.save(otpToken);

        // Enviar email real
        emailService.enviarCodigoOtp(email, codigo);

        return AuthResponseDto.builder()
                .email(email)
                .mensaje("Código enviado a tu email")
                .build();
    }

    public AuthResponseDto verificarCodigo(VerifyOtpDto request) {
        String email = request.getEmail().toLowerCase();
        String codigo = request.getCodigo();

        // Buscar token válido
        Optional<OtpToken> tokenOpt = otpTokenRepository.findValidToken(
                email, codigo, LocalDateTime.now());

        if (tokenOpt.isEmpty()) {
            throw new RuntimeException("Código inválido o expirado");
        }

        OtpToken token = tokenOpt.get();
        token.setUsado(true);
        otpTokenRepository.save(token);

        // Buscar o crear usuario
        Usuario usuario;
        boolean nuevoUsuario = false;

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            usuario = Usuario.builder()
                    .email(email)
                    .nombre(request.getNombre() != null ? request.getNombre() : "Usuario")
                    .fechaRegistro(LocalDateTime.now())
                    .ultimoAcceso(LocalDateTime.now())
                    .activo(true)
                    .emailVerificado(true)
                    .build();
            usuario = usuarioRepository.save(usuario);
            nuevoUsuario = true;
        } else {
            usuario = usuarioOpt.get();
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuario.setEmailVerificado(true);
            usuario = usuarioRepository.save(usuario);
        }

        // Generar JWT
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(usuario.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        return AuthResponseDto.builder()
                .token(jwtToken)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .fotoUrl(usuario.getFotoUrl())
                .nuevoUsuario(nuevoUsuario)
                .mensaje(nuevoUsuario ? "Usuario creado exitosamente" : "Inicio de sesión exitoso")
                .build();
    }

    public AuthResponseDto reenviarCodigo(AuthRequestDto request) {
        String email = request.getEmail().toLowerCase();

        // Verificar que el usuario existe
        if (!usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // Invalidar tokens anteriores
        otpTokenRepository.deleteByEmailAndUsadoTrue(email);

        return solicitarCodigo(request);
    }
}
