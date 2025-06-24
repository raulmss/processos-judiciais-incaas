package com.incaas.tjrn.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "processos_judiciais")
@Getter
@Setter
@NoArgsConstructor
public class ProcessoJudicial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_processo", nullable = false, unique = true)
    @NotBlank(message = "O número do processo é obrigatório")
    @Pattern(
            regexp = "\\d{7}-\\d{2}\\.\\d{4}\\.\\d\\.\\d{2}\\.\\d{4}",
            message = "Formato inválido para o número do processo. Ex: 1234567-89.2023.1.12.3456"
    )
    private String numeroProcesso;

    @NotBlank(message = "A vara é obrigatória")
    private String vara;

    @NotBlank(message = "A comarca é obrigatória")
    private String comarca;

    @NotBlank(message = "O assunto é obrigatório")
    private String assunto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProcesso status;

    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Audiencia> audiencias = new ArrayList<>();
}
