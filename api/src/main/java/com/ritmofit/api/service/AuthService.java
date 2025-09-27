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
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    private static final SecureRandom random = new SecureRandom();

    public AuthResponseDto login(AuthRequestDto request) {
        String email = request.getEmail().toLowerCase();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (usuario.getPassword() == null || !passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        if (!Boolean.TRUE.equals(usuario.getEmailVerificado())) {
            throw new RuntimeException("El email no está verificado");
        }

        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(usuario.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        return AuthResponseDto.builder()
                .token(jwtToken)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .fotoUrl(usuario.getFotoUrl())
                .nuevoUsuario(false)
                .mensaje("Inicio de sesión exitoso")
                .build();
    }

    public AuthResponseDto register(RegisterRequestDto request) {
        String email = request.getEmail().toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .email(email)
                .nombre(request.getNombre())
                .password(passwordEncoder.encode(request.getPassword()))
                .fechaRegistro(LocalDateTime.now())
                .ultimoAcceso(LocalDateTime.now())
                .activo(true)
                .emailVerificado(false)
                .build();

        usuarioRepository.save(usuario);
        return reenviarOtp(email);
    }

    public AuthResponseDto verificarCodigo(VerifyOtpDto request) {
        String email = request.getEmail().toLowerCase();
        String codigo = request.getCodigo();

        Optional<OtpToken> tokenOpt = otpTokenRepository.findValidToken(email, codigo, LocalDateTime.now());
        if (tokenOpt.isEmpty()) {
            throw new RuntimeException("Código inválido o expirado");
        }

        OtpToken token = tokenOpt.get();
        token.setUsado(true);
        otpTokenRepository.save(token);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setEmailVerificado(true);
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(usuario.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        return AuthResponseDto.builder()
                .token(jwtToken)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .fotoUrl(usuario.getFotoUrl())
                .nuevoUsuario(false)
                .mensaje("Email verificado correctamente")
                .build();
    }

    public AuthResponseDto reenviarCodigo(AuthRequestDto request) {
        String email = request.getEmail().toLowerCase();
        return reenviarOtp(email);
    }

    private AuthResponseDto reenviarOtp(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        otpTokenRepository.deleteByEmail(email);

        String codigo = String.format("%06d", random.nextInt(1_000_000));
        OtpToken otpToken = OtpToken.builder()
                .email(email)
                .codigo(codigo)
                .fechaCreacion(LocalDateTime.now())
                .fechaExpiracion(LocalDateTime.now().plusMinutes(10))
                .usado(false)
                .tipo(OtpToken.TipoOtp.LOGIN)
                .build();

        otpTokenRepository.save(otpToken);
        emailService.enviarCodigoOtp(email, codigo);

        return AuthResponseDto.builder()
                .email(email)
                .mensaje("Código enviado a tu email")
                .build();
    }

    public boolean validarToken(String token) {
        try {
            jwtService.validateToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

