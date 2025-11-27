package com.ritmofit.api.repository;

import com.ritmofit.api.model.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    @Query("SELECT n FROM Notificacion n " +
       "JOIN FETCH n.clase c " +
       "WHERE n.usuarioId = :usuarioId " +
       "AND n.enviada = false " +
       "AND n.fechaEnvio <= :ahora")
     List<Notificacion> findPendientesConClaseCargada(@Param("usuarioId") Long usuarioId,@Param("ahora") LocalDateTime ahora);

    List<Notificacion> findByUsuarioIdAndEnviadaFalseAndFechaEnvioBefore(
            Long usuarioId,
            LocalDateTime fecha
    );

    boolean existsByClaseIdAndTipo(Long claseId, String tipo);

    boolean existsByClaseIdAndTipoAndUsuarioId(Long claseId, String tipo, Long usuarioId);

    Optional<Notificacion> findByClaseIdAndTipoAndUsuarioId(
            Long claseId,
            String tipo,
            Long usuarioId
    );
}
