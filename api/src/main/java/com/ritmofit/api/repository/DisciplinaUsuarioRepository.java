package com.ritmofit.api.repository;

import com.ritmofit.api.model.entity.DisciplinaUsuario;
import com.ritmofit.api.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisciplinaUsuarioRepository extends JpaRepository<DisciplinaUsuario, Long> {

    @Query("SELECT du FROM DisciplinaUsuario du WHERE du.usuario.id = :usuarioId")
    List<DisciplinaUsuario> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT du FROM DisciplinaUsuario du WHERE du.usuario = :usuario")
    List<DisciplinaUsuario> findByUsuario(@Param("usuario") Usuario usuario);

    @Query("SELECT du FROM DisciplinaUsuario du WHERE du.usuario.id = :usuarioId AND du.disciplina.id = :disciplinaId")
    Optional<DisciplinaUsuario> findByUsuarioIdAndDisciplinaId(@Param("usuarioId") Long usuarioId, @Param("disciplinaId") Long disciplinaId);

    @Query("SELECT COUNT(du) > 0 FROM DisciplinaUsuario du WHERE du.usuario.id = :usuarioId AND du.disciplina.id = :disciplinaId")
    boolean existsByUsuarioIdAndDisciplinaId(@Param("usuarioId") Long usuarioId, @Param("disciplinaId") Long disciplinaId);
}
