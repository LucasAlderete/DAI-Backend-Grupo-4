package com.ritmofit.api.service;

import com.ritmofit.api.dto.SedeUsuarioDto;
import com.ritmofit.api.model.entity.Sede;
import com.ritmofit.api.model.entity.SedeUsuario;
import com.ritmofit.api.model.entity.Usuario;
import com.ritmofit.api.repository.SedeRepository;
import com.ritmofit.api.repository.SedeUsuarioRepository;
import com.ritmofit.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SedeUsuarioService {

    private final SedeUsuarioRepository sedeUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final SedeRepository sedeRepository;

    public List<SedeUsuarioDto> obtenerSedesFavoritasPorUsuario(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return sedeUsuarioRepository.findByUsuario(usuario).stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    public SedeUsuarioDto agregarSedeFavorita(String emailUsuario, Long sedeId) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Sede sede = sedeRepository.findById(sedeId)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));

        // Verificar si ya existe
        if (sedeUsuarioRepository.existsByUsuarioIdAndSedeId(usuario.getId(), sedeId)) {
            throw new RuntimeException("La sede ya está marcada como favorita");
        }

        SedeUsuario sedeUsuario = SedeUsuario.builder()
                .usuario(usuario)
                .sede(sede)
                .build();

        sedeUsuario = sedeUsuarioRepository.save(sedeUsuario);
        return convertirADto(sedeUsuario);
    }

    public void eliminarSedeFavorita(String emailUsuario, Long sedeUsuarioId) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        SedeUsuario sedeUsuario = sedeUsuarioRepository.findById(sedeUsuarioId)
                .orElseThrow(() -> new RuntimeException("Sede favorita no encontrada"));

        // Verificar que pertenece al usuario
        if (!sedeUsuario.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permisos para eliminar esta sede favorita");
        }

        sedeUsuarioRepository.delete(sedeUsuario);
    }

    private SedeUsuarioDto convertirADto(SedeUsuario sedeUsuario) {
        return SedeUsuarioDto.builder()
                .id(sedeUsuario.getId())
                .usuarioId(sedeUsuario.getUsuario().getId())
                .sedeId(sedeUsuario.getSede().getId())
                .sedeNombre(sedeUsuario.getSede().getNombre())
                .sedeDireccion(sedeUsuario.getSede().getDireccion())
                .build();
    }
}
