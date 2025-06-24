package com.incaas.tjrn.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Audiencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A data e hora de início é obrigatória")
    @Future(message = "A data e hora de início deve estar no futuro")
    private Instant dataHoraInicio;

    @NotNull(message = "A data e hora de fim é obrigatória")
    @Future(message = "A data e hora de fim deve estar no futuro")
    private Instant dataHoraFim;

    @NotNull(message = "O tipo de audiência é obrigatório")
    @Enumerated(EnumType.STRING)
    private TipoAudiencia tipo;

    @Embedded
    @Valid
    private LocalAudiencia local;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processo_id", nullable = false)
    private ProcessoJudicial processo;
}
