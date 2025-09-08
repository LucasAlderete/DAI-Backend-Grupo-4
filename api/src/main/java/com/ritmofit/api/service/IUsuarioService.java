package com.ritmofit.api.service;

import com.ritmofit.api.dto.UsuarioDto;
import com.ritmofit.api.model.entity.Usuario;

import java.util.Optional;

public interface IUsuarioService {
    Usuario crearUsuario(UsuarioDto usuarioDto);
    Optional<Usuario> obtenerUsuario(Long id);
    Usuario actualizarUsuario(Long id, UsuarioDto usuarioDto);
}
