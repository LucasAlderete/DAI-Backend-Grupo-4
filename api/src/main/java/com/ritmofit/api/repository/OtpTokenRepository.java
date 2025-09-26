package com.ritmofit.api.repository;

import com.ritmofit.api.model.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    @Query("""
        SELECT ot 
        FROM OtpToken ot 
        WHERE ot.email = :email 
          AND ot.codigo = :codigo 
          AND ot.usado = false 
          AND ot.fechaExpiracion > :now
    """)
    Optional<OtpToken> findValidToken(
            @Param("email") String email,
            @Param("codigo") String codigo,
            @Param("now") LocalDateTime now
    );

    @Query("""
        SELECT ot 
        FROM OtpToken ot 
        WHERE ot.email = :email 
          AND ot.usado = false 
          AND ot.fechaExpiracion > :now 
        ORDER BY ot.fechaCreacion DESC
    """)
    Optional<OtpToken> findLatestValidToken(
            @Param("email") String email,
            @Param("now") LocalDateTime now
    );

    void deleteByEmailAndUsadoTrue(String email);

    // ✅ Nuevo: elimina todos los tokens asociados a un email
    void deleteByEmail(String email);
}
