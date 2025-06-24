package com.incaas.tjrn.mapper;

import com.incaas.tjrn.dto.request.ProcessoJudicialRequestDTO;
import com.incaas.tjrn.dto.response.ProcessoJudicialResponseDTO;
import com.incaas.tjrn.model.ProcessoJudicial;
import com.incaas.tjrn.model.StatusProcesso;

public class ProcessoJudicialMapper {

        public static ProcessoJudicial requestDtoParaEntidade(ProcessoJudicialRequestDTO dto) {
            return criarEntidadeComBaseNoDto(dto);
        }

        public static ProcessoJudicialRequestDTO entidadeParaRequestDto(ProcessoJudicial processo) {
            return ProcessoJudicialRequestDTO.builder()
                    .numeroProcesso(processo.getNumeroProcesso())
                    .vara(processo.getVara())
                    .comarca(processo.getComarca())
                    .assunto(processo.getAssunto())
                    .status(processo.getStatus().name())
                    .build();
        }

        public static ProcessoJudicialResponseDTO entidadeParaResponseDto(ProcessoJudicial processo) {
            return ProcessoJudicialResponseDTO.builder()
                    .id(processo.getId())
                    .numeroProcesso(processo.getNumeroProcesso())
                    .vara(processo.getVara())
                    .comarca(processo.getComarca())
                    .assunto(processo.getAssunto())
                    .status(processo.getStatus())
                    .build();
        }

        private static ProcessoJudicial criarEntidadeComBaseNoDto(ProcessoJudicialRequestDTO dto) {
            ProcessoJudicial processo = new ProcessoJudicial();
            processo.setNumeroProcesso(dto.numeroProcesso());
            processo.setVara(dto.vara());
            processo.setComarca(dto.comarca());
            processo.setAssunto(dto.assunto());
            processo.setStatus(StatusProcesso.valueOf(dto.status().toUpperCase()));
            return processo;
        }
}
