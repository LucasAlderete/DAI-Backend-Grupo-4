package com.ritmofit.api.service;

import com.ritmofit.api.dto.UsuarioDto;
import com.ritmofit.api.model.entity.Usuario;
import com.ritmofit.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDto obtenerPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return mapToDto(usuario);
    }

    public UsuarioDto actualizarPerfil(String email, UsuarioDto usuarioDto) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(usuarioDto.getNombre());
        if (usuarioDto.getFotoUrl() != null) {
            usuario.setFotoUrl(usuarioDto.getFotoUrl());
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
                .fotoUrl(usuarioDto.getFotoUrl())
                .activo(true)
                .emailVerificado(true)
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
