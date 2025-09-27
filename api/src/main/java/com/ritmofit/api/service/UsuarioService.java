package com.ritmofit.api.service;

import com.ritmofit.api.dto.UsuarioDto;
import com.ritmofit.api.dto.UsuarioDtoUpdate;
import com.ritmofit.api.model.entity.Usuario;
import com.ritmofit.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_IMAGE_URL = "/images/default-profile.png";
    private static final String UPLOAD_DIR = "uploads/";


    public UsuarioDto obtenerPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String fotoUrl = (usuario.getFotoUrl() == null || usuario.getFotoUrl().isEmpty())
                ? DEFAULT_IMAGE_URL
                : usuario.getFotoUrl();

        return UsuarioDto.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .fotoUrl(fotoUrl)
                .build();
    }

    public UsuarioDto actualizarPerfil(String email, UsuarioDtoUpdate usuarioDtoUpdate) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

usuario.setNombre(usuarioDtoUpdate.getNombre());
        usuario = usuarioRepository.save(usuario);

        String fotoUrl = (usuario.getFotoUrl() == null || usuario.getFotoUrl().isEmpty())
                ? DEFAULT_IMAGE_URL
                : usuario.getFotoUrl();

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


    public String actualizarImagenPerfil(String email, MultipartFile imagen) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (imagen.isEmpty()) {
            throw new IllegalArgumentException("La imagen está vacía");
        }

        try {
            String nombreArchivo = UUID.randomUUID() + "_" + imagen.getOriginalFilename();
            Path ruta = Paths.get(UPLOAD_DIR + nombreArchivo);
            Files.createDirectories(ruta.getParent());
            Files.copy(imagen.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);

            String urlImagen = "/" + UPLOAD_DIR + nombreArchivo;

            usuario.setFotoUrl(urlImagen);
            usuarioRepository.save(usuario);

            return urlImagen;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen", e);
        }
    }


}
