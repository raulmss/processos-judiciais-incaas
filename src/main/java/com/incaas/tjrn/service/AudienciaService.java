package com.incaas.tjrn.service;

import com.incaas.tjrn.dto.request.AudienciaRequestDTO;
import com.incaas.tjrn.dto.response.AudienciaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AudienciaService {

    AudienciaResponseDTO agendarAudiencia(AudienciaRequestDTO dto);

    Page<AudienciaResponseDTO> consultarAgenda(String comarca, LocalDate data, Pageable pageable);

    AudienciaResponseDTO atualizarAudiencia(Long id, AudienciaRequestDTO dto);

    void deletarAudiencia(Long id);
}
