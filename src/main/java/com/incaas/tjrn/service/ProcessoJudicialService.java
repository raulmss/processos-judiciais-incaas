package com.incaas.tjrn.service;

import com.incaas.tjrn.dto.request.ProcessoJudicialRequestDTO;
import com.incaas.tjrn.dto.response.ProcessoJudicialResponseDTO;
import com.incaas.tjrn.model.StatusProcesso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProcessoJudicialService {

    ProcessoJudicialResponseDTO create(ProcessoJudicialRequestDTO dto);

    Page<ProcessoJudicialResponseDTO> findByFilter(StatusProcesso status, String comarca, Pageable pageable);

    ProcessoJudicialResponseDTO update(Long id, ProcessoJudicialRequestDTO dto);

    void delete(Long id);
}