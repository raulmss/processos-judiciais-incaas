package com.incaas.tjrn.repository;

import com.incaas.tjrn.model.ProcessoJudicial;
import com.incaas.tjrn.model.StatusProcesso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProcessoJudicialRepository extends JpaRepository<ProcessoJudicial, Long> {

    @Query("""
        SELECT p FROM ProcessoJudicial p
        WHERE (:status IS NULL OR p.status = :status)
          AND (:comarca IS NULL OR LOWER(p.comarca) LIKE LOWER(CONCAT('%', :comarca, '%')))
    """)
    Page<ProcessoJudicial> filtrar(
            @Param("status") StatusProcesso status,
            @Param("comarca") String comarca,
            Pageable pageable
    );

    boolean existsByNumeroProcesso(String numeroProcesso);
}