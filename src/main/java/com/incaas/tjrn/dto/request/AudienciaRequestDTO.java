package com.incaas.tjrn.dto.request;

import com.incaas.tjrn.dto.shared.LocalAudienciaDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;

@Builder
public record AudienciaRequestDTO(

        @NotNull(message = "A data e hora de início é obrigatória")
        @Future(message = "A data e hora de início deve estar no futuro")
        Instant dataHoraInicio,

        @NotNull(message = "A data e hora de fim é obrigatória")
        @Future(message = "A data e hora de fim deve estar no futuro")
        Instant dataHoraFim,

        @NotBlank(message = "O tipo da audiência é obrigatório")
        String tipo,

        @NotNull(message = "O local da audiência é obrigatório")
        @Valid
        LocalAudienciaDTO local,

        @NotNull(message = "O ID do processo é obrigatório")
        Long processoId
) {}
