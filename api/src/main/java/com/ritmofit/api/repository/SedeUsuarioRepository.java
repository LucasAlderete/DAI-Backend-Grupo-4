package com.ritmofit.api.repository;

import com.ritmofit.api.model.entity.SedeUsuario;
import com.ritmofit.api.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SedeUsuarioRepository extends JpaRepository<SedeUsuario, Long> {

    @Query("SELECT su FROM SedeUsuario su WHERE su.usuario.id = :usuarioId")
    List<SedeUsuario> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT su FROM SedeUsuario su WHERE su.usuario = :usuario")
    List<SedeUsuario> findByUsuario(@Param("usuario") Usuario usuario);

    @Query("SELECT su FROM SedeUsuario su WHERE su.usuario.id = :usuarioId AND su.sede.id = :sedeId")
    Optional<SedeUsuario> findByUsuarioIdAndSedeId(@Param("usuarioId") Long usuarioId, @Param("sedeId") Long sedeId);

    @Query("SELECT COUNT(su) > 0 FROM SedeUsuario su WHERE su.usuario.id = :usuarioId AND su.sede.id = :sedeId")
    boolean existsByUsuarioIdAndSedeId(@Param("usuarioId") Long usuarioId, @Param("sedeId") Long sedeId);
}
