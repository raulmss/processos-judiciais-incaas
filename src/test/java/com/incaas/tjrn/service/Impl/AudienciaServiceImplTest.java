
package com.incaas.tjrn.service.impl;

import com.incaas.tjrn.dto.request.AudienciaRequestDTO;
import com.incaas.tjrn.dto.shared.LocalAudienciaDTO;
import com.incaas.tjrn.exception.exceptions.RecursoNaoEncontradoException;
import com.incaas.tjrn.exception.exceptions.ViolacaoDeRegraNegocioException;
import com.incaas.tjrn.service.impl.AudienciaServiceImpl;
import com.incaas.tjrn.model.Audiencia;
import com.incaas.tjrn.model.LocalAudiencia;
import com.incaas.tjrn.model.ProcessoJudicial;
import com.incaas.tjrn.model.StatusProcesso;
import com.incaas.tjrn.repository.AudienciaRepository;
import com.incaas.tjrn.repository.ProcessoJudicialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AudienciaServiceImplTest {

    @Mock
    private AudienciaRepository audienciaRepository;

    @Mock
    private ProcessoJudicialRepository processoRepository;

    @InjectMocks
    private AudienciaServiceImpl audienciaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveLancarExcecaoQuandoProcessoNaoExiste() {
        when(processoRepository.findById(anyLong())).thenReturn(Optional.empty());

        AudienciaRequestDTO dto = AudienciaRequestDTO.builder()
                .processoId(1L)
                .dataHoraInicio(Instant.now().plus(2, ChronoUnit.DAYS))
                .dataHoraFim(Instant.now().plus(3, ChronoUnit.DAYS))
                .tipo("CONCILIACAO")
                .local(LocalAudienciaDTO.builder().nome("Sala 1").build())
                .build();

        assertThrows(ViolacaoDeRegraNegocioException.class, () -> audienciaService.agendarAudiencia(dto));
    }

    @Test
    void deveLancarExcecaoQuandoProcessoArquivado() {
        ProcessoJudicial processo = new ProcessoJudicial();
        processo.setStatus(StatusProcesso.ARQUIVADO);

        when(processoRepository.findById(anyLong())).thenReturn(Optional.of(processo));

        AudienciaRequestDTO dto = AudienciaRequestDTO.builder()
                .processoId(1L)
                .dataHoraInicio(Instant.now().plus(2, ChronoUnit.DAYS))
                .dataHoraFim(Instant.now().plus(3, ChronoUnit.DAYS))
                .tipo("INSTRUCAO")
                .local(LocalAudienciaDTO.builder().nome("Sala 2").build())
                .build();

        assertThrows(ViolacaoDeRegraNegocioException.class, () -> audienciaService.agendarAudiencia(dto));
    }

    @Test
    void deveLancarExcecaoQuandoProcessoSuspenso() {
        ProcessoJudicial processo = new ProcessoJudicial();
        processo.setStatus(StatusProcesso.SUSPENSO);

        when(processoRepository.findById(anyLong())).thenReturn(Optional.of(processo));

        AudienciaRequestDTO dto = AudienciaRequestDTO.builder()
                .processoId(1L)
                .dataHoraInicio(Instant.now().plus(2, ChronoUnit.DAYS))
                .dataHoraFim(Instant.now().plus(3, ChronoUnit.DAYS))
                .tipo("INSTRUCAO")
                .local(LocalAudienciaDTO.builder().nome("Sala 2").build())
                .build();

        assertThrows(ViolacaoDeRegraNegocioException.class, () -> audienciaService.agendarAudiencia(dto));
    }

    @Test
    void deveLancarExcecaoQuandoDataEhNoFimDeSemana() {
        ProcessoJudicial processo = new ProcessoJudicial();
        processo.setStatus(StatusProcesso.ATIVO);

        Instant sabado = LocalDate.of(2025, 6, 28).atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(processoRepository.findById(anyLong())).thenReturn(Optional.of(processo));

        AudienciaRequestDTO dto = AudienciaRequestDTO.builder()
                .processoId(1L)
                .dataHoraInicio(sabado)
                .dataHoraFim(sabado.plus(1, ChronoUnit.HOURS))
                .tipo("JULGAMENTO")
                .local(LocalAudienciaDTO.builder().nome("Sala 3").build())
                .build();

        assertThrows(ViolacaoDeRegraNegocioException.class, () -> audienciaService.agendarAudiencia(dto));
    }

    @Test
    void deveLancarExcecaoQuandoHaConflitoDeAudiencia() {
        ProcessoJudicial processo = new ProcessoJudicial();
        processo.setStatus(StatusProcesso.ATIVO);
        processo.setVara("Vara 1");

        Instant inicio = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant fim = inicio.plus(1, ChronoUnit.HOURS);

        when(processoRepository.findById(anyLong())).thenReturn(Optional.of(processo));
        when(audienciaRepository.existsConflitoDeAudiencia(inicio, fim, "Sala 4", "Vara 1", null)).thenReturn(true);

        AudienciaRequestDTO dto = AudienciaRequestDTO.builder()
                .processoId(1L)
                .dataHoraInicio(inicio)
                .dataHoraFim(fim)
                .tipo("CONCILIACAO")
                .local(LocalAudienciaDTO.builder().nome("Sala 4").build())
                .build();

        assertThrows(ViolacaoDeRegraNegocioException.class, () -> audienciaService.agendarAudiencia(dto));
    }

    @Test
    void deveLancarExcecaoQuandoAudienciaNaoExisteParaAtualizacao() {
        when(audienciaRepository.findById(anyLong())).thenReturn(Optional.empty());

        AudienciaRequestDTO dto = AudienciaRequestDTO.builder()
                .processoId(1L)
                .dataHoraInicio(Instant.now().plus(1, ChronoUnit.DAYS))
                .dataHoraFim(Instant.now().plus(2, ChronoUnit.DAYS))
                .tipo("JULGAMENTO")
                .local(LocalAudienciaDTO.builder().nome("Sala 5").build())
                .build();

        assertThrows(RecursoNaoEncontradoException.class, () -> audienciaService.atualizarAudiencia(1L, dto));
    }
}