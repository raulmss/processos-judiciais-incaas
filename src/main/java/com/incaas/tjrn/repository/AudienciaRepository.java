package com.incaas.tjrn.repository;

import com.incaas.tjrn.model.Audiencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface AudienciaRepository extends JpaRepository<Audiencia, Long> {

    @Query("""
    SELECT COUNT(a) > 0
    FROM Audiencia a
    WHERE a.local.nome = :localNome
      AND a.processo.vara = :vara
      AND a.dataHoraInicio < :fim
      AND a.dataHoraFim > :inicio
      AND (:audienciaId IS NULL OR a.id <> :audienciaId)
""")
    boolean existsConflitoDeAudiencia(
            @Param("inicio") Instant inicio,
            @Param("fim") Instant fim,
            @Param("localNome") String localNome,
            @Param("vara") String vara,
            @Param("audienciaId") Long audienciaId // nullable when creating
    );

    @Query("""
    SELECT a FROM Audiencia a
    WHERE (:comarca IS NULL OR a.processo.comarca = :comarca)
      AND (:inicio IS NULL OR :fim IS NULL OR (a.dataHoraInicio < :fim AND a.dataHoraFim > :inicio))
    ORDER BY a.dataHoraInicio
""")
    Page<Audiencia> findByComarcaAndDataOpcional(
            @Param("comarca") String comarca,
            @Param("inicio") Instant inicio,
            @Param("fim") Instant fim,
            Pageable pageable
    );

}
