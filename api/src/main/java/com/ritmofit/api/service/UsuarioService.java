package com.ritmofit.api.service;

import com.ritmofit.api.dto.UsuarioDto;
import com.ritmofit.api.dto.UsuarioDtoUpdate;
import com.ritmofit.api.model.entity.Usuario;
import com.ritmofit.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioDto obtenerPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return mapToDto(usuario);
    }

    public UsuarioDto actualizarPerfil(String email, UsuarioDtoUpdate usuarioDtoUpdate) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizamos solo los campos que no sean null
        if (usuarioDtoUpdate.getNombre() != null) {
            usuario.setNombre(usuarioDtoUpdate.getNombre());
        }

        if (usuarioDtoUpdate.getEmail() != null) {
            usuario.setEmail(usuarioDtoUpdate.getEmail());
        }

        if (usuarioDtoUpdate.getPassword() != null) {
            usuario.setPassword(passwordEncoder.encode(usuarioDtoUpdate.getPassword()));
        }

        if (usuarioDtoUpdate.getFotoUrl() != null) {
            usuario.setFotoUrl(usuarioDtoUpdate.getFotoUrl());
        }

        usuario.setUltimoAcceso(LocalDateTime.now());
        usuario = usuarioRepository.save(usuario);

        return mapToDto(usuario);
    }

    public UsuarioDto registrarUsuario(UsuarioDto usuarioDto) {
        if (usuarioRepository.existsByEmail(usuarioDto.getEmail().toLowerCase())) {
            throw new RuntimeException("Usuario ya registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(usuarioDto.getNombre())
                .email(usuarioDto.getEmail().toLowerCase())
                .password(passwordEncoder.encode(usuarioDto.getPassword()))
                .fotoUrl(usuarioDto.getFotoUrl())
                .activo(true)
                .emailVerificado(false) // recién verificado al validar OTP
                .fechaRegistro(LocalDateTime.now())
                .ultimoAcceso(LocalDateTime.now())
                .build();

        usuario = usuarioRepository.save(usuario);

        return mapToDto(usuario);
    }

    private UsuarioDto mapToDto(Usuario usuario) {
        return UsuarioDto.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .fotoUrl(usuario.getFotoUrl())
                .activo(usuario.getActivo())
                .emailVerificado(usuario.getEmailVerificado())
                .fechaRegistro(usuario.getFechaRegistro())
                .ultimoAcceso(usuario.getUltimoAcceso())
                .build();
    }
}
