package com.incaas.tjrn.dto.response;

import com.incaas.tjrn.model.StatusProcesso;
import lombok.Builder;

@Builder
public record ProcessoJudicialResponseDTO(
        Long id,
        String numeroProcesso,
        String vara,
        String comarca,
        String assunto,
        StatusProcesso status
) {}