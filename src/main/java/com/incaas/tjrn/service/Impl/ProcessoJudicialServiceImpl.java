package com.incaas.tjrn.service.Impl;

import com.incaas.tjrn.dto.request.ProcessoJudicialRequestDTO;
import com.incaas.tjrn.dto.response.ProcessoJudicialResponseDTO;
import com.incaas.tjrn.exception.exceptions.ProcessoJudicialComNumeroJaExisteException;
import com.incaas.tjrn.exception.exceptions.RecursoNaoEncontradoException;
import com.incaas.tjrn.exception.exceptions.ViolacaoDeRegraNegocioException;
import com.incaas.tjrn.mapper.ProcessoJudicialMapper;
import com.incaas.tjrn.model.ProcessoJudicial;
import com.incaas.tjrn.model.StatusProcesso;
import com.incaas.tjrn.repository.ProcessoJudicialRepository;
import com.incaas.tjrn.service.ProcessoJudicialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProcessoJudicialServiceImpl implements ProcessoJudicialService {

    @Autowired
    private ProcessoJudicialRepository repository;

    @Override
    public ProcessoJudicialResponseDTO create(ProcessoJudicialRequestDTO dto) {
        // Verifica se já existe um processo com o mesmo número
        if (repository.existsByNumeroProcesso(dto.numeroProcesso())) {
            throw new ProcessoJudicialComNumeroJaExisteException(dto.numeroProcesso());
        }

        ProcessoJudicial processo = ProcessoJudicialMapper.requestDtoParaEntidade(dto);
        ProcessoJudicial salvo = repository.save(processo);
        return ProcessoJudicialMapper.entidadeParaResponseDto(salvo);
    }

    @Override
    public Page<ProcessoJudicialResponseDTO> findByFilter(StatusProcesso status, String comarca, Pageable pageable) {
        return repository.filtrar(status, comarca, pageable)
                .map(ProcessoJudicialMapper::entidadeParaResponseDto);
    }

    @Override
    public ProcessoJudicialResponseDTO update(Long id, ProcessoJudicialRequestDTO dto) {
        ProcessoJudicial existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Processo não encontrado com ID: " + id));

        if (!existente.getNumeroProcesso().equals(dto.numeroProcesso())) {
            throw new ViolacaoDeRegraNegocioException("Não é permitido alterar o número do processo.");
        }
        existente.setVara(dto.vara());
        existente.setComarca(dto.comarca());
        existente.setAssunto(dto.assunto());
        existente.setStatus(StatusProcesso.valueOf(dto.status().toUpperCase()));

        ProcessoJudicial atualizado = repository.save(existente);
        return ProcessoJudicialMapper.entidadeParaResponseDto(atualizado);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Processo não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}
