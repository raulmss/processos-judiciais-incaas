package com.incaas.tjrn.dto.shared;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LocalAudienciaDTO(

        @NotBlank(message = "O nome do local é obrigatório")
        String nome,

        @NotBlank(message = "A rua é obrigatória")
        String rua,

        @NotBlank(message = "O número é obrigatório")
        String numero,

        @NotBlank(message = "O bairro é obrigatório")
        String bairro,

        @NotBlank(message = "O CEP é obrigatório")
        String cep,

        @NotBlank(message = "O estado é obrigatório")
        String estado,

        @NotBlank(message = "O país é obrigatório")
        String pais
) {}
