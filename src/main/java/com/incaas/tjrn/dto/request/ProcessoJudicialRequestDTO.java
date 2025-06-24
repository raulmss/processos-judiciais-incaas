package com.incaas.tjrn.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record ProcessoJudicialRequestDTO(

        @NotBlank(message = "O número do processo é obrigatório")
        @Pattern(
                regexp = "^\\d{7}-\\d{2}\\.\\d{4}\\.\\d\\.\\d{2}\\.\\d{4}$",
                message = "Formato inválido. Use o padrão: 1234567-89.2023.1.12.0001"
        )
        String numeroProcesso,

        @NotBlank(message = "A vara é obrigatória")
        String vara,

        @NotBlank(message = "A comarca é obrigatória")
        String comarca,

        @NotBlank(message = "O assunto é obrigatório")
        String assunto,

        @NotBlank(message = "O status é obrigatório")
        String status
) {}
