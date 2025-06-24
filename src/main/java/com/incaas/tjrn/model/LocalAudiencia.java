package com.incaas.tjrn.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class LocalAudiencia {

    @NotBlank(message = "O nome do local é obrigatório")
    private String nome;

    @NotBlank(message = "A rua é obrigatória")
    private String rua;

    @NotBlank(message = "O número é obrigatório")
    private String numero;

    @NotBlank(message = "O bairro é obrigatório")
    private String bairro;

    @NotBlank(message = "O CEP é obrigatório")
    private String cep;

    @NotBlank(message = "O estado é obrigatório")
    private String estado;

    @NotBlank(message = "O país é obrigatório")
    private String pais;
}
