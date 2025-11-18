package com.ritmofit.api.repository;

import com.ritmofit.api.model.entity.DiaSemana;
import com.ritmofit.api.model.entity.DisponibilidadHorariaUsuario;
import com.ritmofit.api.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisponibilidadHorariaUsuarioRepository extends JpaRepository<DisponibilidadHorariaUsuario, Long> {

    @Query("SELECT dh FROM DisponibilidadHorariaUsuario dh WHERE dh.usuario.id = :usuarioId AND dh.activo = true")
    List<DisponibilidadHorariaUsuario> findByUsuarioIdAndActivoTrue(@Param("usuarioId") Long usuarioId);

    @Query("SELECT dh FROM DisponibilidadHorariaUsuario dh WHERE dh.usuario = :usuario AND dh.activo = true")
    List<DisponibilidadHorariaUsuario> findByUsuarioAndActivoTrue(@Param("usuario") Usuario usuario);

    @Query("SELECT dh FROM DisponibilidadHorariaUsuario dh WHERE dh.usuario.id = :usuarioId AND dh.diaSemana = :diaSemana AND dh.activo = true")
    List<DisponibilidadHorariaUsuario> findByUsuarioIdAndDiaSemanaAndActivoTrue(@Param("usuarioId") Long usuarioId, @Param("diaSemana") DiaSemana diaSemana);

    @Query("SELECT dh FROM DisponibilidadHorariaUsuario dh WHERE dh.usuario.id = :usuarioId")
    List<DisponibilidadHorariaUsuario> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
