package com.incaas.tjrn.service.impl;

import com.incaas.tjrn.dto.request.AudienciaRequestDTO;
import com.incaas.tjrn.dto.response.AudienciaResponseDTO;
import com.incaas.tjrn.exception.exceptions.RecursoNaoEncontradoException;
import com.incaas.tjrn.exception.exceptions.ViolacaoDeRegraNegocioException;
import com.incaas.tjrn.mapper.AudienciaMapper;
import com.incaas.tjrn.model.Audiencia;
import com.incaas.tjrn.model.ProcessoJudicial;
import com.incaas.tjrn.model.StatusProcesso;
import com.incaas.tjrn.repository.AudienciaRepository;
import com.incaas.tjrn.repository.ProcessoJudicialRepository;
import com.incaas.tjrn.service.AudienciaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
@RequiredArgsConstructor
public class AudienciaServiceImpl implements AudienciaService {

    private final AudienciaRepository audienciaRepository;
    private final ProcessoJudicialRepository processoRepository;

    @Override
    @Transactional
    public AudienciaResponseDTO agendarAudiencia(AudienciaRequestDTO dto) {
        ProcessoJudicial processo = validarProcessoAtivo(dto.processoId());
        validarDataUtil(dto.dataHoraInicio());
        validarDataUtil(dto.dataHoraFim());
        validarConflito(dto.dataHoraInicio(), dto.dataHoraFim(), dto.local().nome(), processo.getVara());

        Audiencia nova = AudienciaMapper.dtoParaEntidade(dto, processo);
        return AudienciaMapper.entidadeParaDto(audienciaRepository.save(nova));
    }


    @Override
    public Page<AudienciaResponseDTO> consultarAgenda(String comarca, LocalDate data, Pageable pageable) {
        Instant inicio = null;
        Instant fim = null;

        if (data != null) {
            inicio = data.atStartOfDay(ZoneId.systemDefault()).toInstant();
            fim = data.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
        }

        return audienciaRepository.findByComarcaAndDataOpcional(comarca, inicio, fim, pageable)
                .map(AudienciaMapper::entidadeParaDto);
    }

    @Override
    @Transactional
    public AudienciaResponseDTO atualizarAudiencia(Long id, AudienciaRequestDTO dto) {
        Audiencia existente = audienciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Audiência não encontrada com ID: " + id));

        ProcessoJudicial processo = validarProcessoAtivo(dto.processoId());
        validarDataUtil(dto.dataHoraInicio());
        validarDataUtil(dto.dataHoraFim());
        validarConflito(dto.dataHoraInicio(), dto.dataHoraFim(), dto.local().nome(), processo.getVara(), id);

        existente.setDataHoraInicio(dto.dataHoraInicio());
        existente.setDataHoraFim(dto.dataHoraFim());
        existente.setTipo(Enum.valueOf(com.incaas.tjrn.model.TipoAudiencia.class, dto.tipo().toUpperCase()));
        existente.setLocal(AudienciaMapper.mapearLocal(dto.local()));
        existente.setProcesso(processo);

        return AudienciaMapper.entidadeParaDto(audienciaRepository.save(existente));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deletarAudiencia(Long id) {
        if (!audienciaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Audiência não encontrada com ID: " + id);
        }
        audienciaRepository.deleteById(id);
    }

    // 🔐 Métodos privados auxiliares:

    private ProcessoJudicial validarProcessoAtivo(Long id) {
        ProcessoJudicial processo = processoRepository.findById(id)
                .orElseThrow(() -> new ViolacaoDeRegraNegocioException("Processo não encontrado"));
        if (processo.getStatus() != StatusProcesso.ATIVO) {
            throw new ViolacaoDeRegraNegocioException("Não é possível agendar audiência para processo " + processo.getStatus());
        }
        return processo;
    }

    private void validarDataUtil(Instant dataHora) {
        DayOfWeek dia = dataHora.atZone(ZoneId.systemDefault()).getDayOfWeek();
        if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
            throw new ViolacaoDeRegraNegocioException("Audiência só pode ser agendada em dias úteis");
        }
    }

    private void validarConflito(Instant inicio, Instant fim, String localNome, String vara, Long idParaIgnorar) {
        if (audienciaRepository.existsConflitoDeAudiencia(inicio, fim, localNome, vara, idParaIgnorar)) {
            throw new ViolacaoDeRegraNegocioException("Conflito: já existe uma audiência nesse intervalo");
        }
    }


    private void validarConflito(Instant inicio, Instant fim, String localNome, String vara) {
        validarConflito(inicio, fim, localNome, vara, null);
    }

}
