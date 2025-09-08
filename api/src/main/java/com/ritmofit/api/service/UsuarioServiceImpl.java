package com.ritmofit.api.service;

import com.ritmofit.api.dto.UsuarioDto;
import com.ritmofit.api.model.entity.Usuario;
import com.ritmofit.api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Usuario crearUsuario(UsuarioDto nuevoUsuario) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nuevoUsuario.getNombre());
        usuario.setEmail(nuevoUsuario.getEmail());
        usuario.setFoto(nuevoUsuario.getFoto());

        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> obtenerUsuario(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario actualizarUsuario(Long id, UsuarioDto usuarioActualizar) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(usuarioActualizar.getNombre());
        usuario.setEmail(usuarioActualizar.getEmail());
        usuario.setFoto(usuarioActualizar.getFoto());

        return usuarioRepository.save(usuario);
    }
}
