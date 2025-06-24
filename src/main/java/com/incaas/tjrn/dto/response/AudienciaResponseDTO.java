package com.incaas.tjrn.dto.response;

import com.incaas.tjrn.dto.shared.LocalAudienciaDTO;
import com.incaas.tjrn.model.TipoAudiencia;
import lombok.Builder;

import java.time.Instant;

@Builder
public record AudienciaResponseDTO(
        Long id,
        Instant dataHoraInicio,
        Instant dataHoraFim,
        TipoAudiencia tipo,
        LocalAudienciaDTO local,
        Long processoId
) {}
