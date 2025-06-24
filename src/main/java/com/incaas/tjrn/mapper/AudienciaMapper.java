package com.incaas.tjrn.mapper;

import com.incaas.tjrn.dto.request.AudienciaRequestDTO;
import com.incaas.tjrn.dto.response.AudienciaResponseDTO;
import com.incaas.tjrn.dto.shared.LocalAudienciaDTO;
import com.incaas.tjrn.model.*;

public class AudienciaMapper {

    public static Audiencia dtoParaEntidade(AudienciaRequestDTO dto, ProcessoJudicial processo) {
        Audiencia audiencia = new Audiencia();
        audiencia.setDataHoraInicio(dto.dataHoraInicio());
        audiencia.setDataHoraFim(dto.dataHoraFim());
        audiencia.setTipo(TipoAudiencia.valueOf(dto.tipo().toUpperCase()));
        audiencia.setLocal(mapearLocal(dto.local()));
        audiencia.setProcesso(processo);
        return audiencia;
    }

    public static AudienciaResponseDTO entidadeParaDto(Audiencia audiencia) {
        return AudienciaResponseDTO.builder()
                .id(audiencia.getId())
                .dataHoraInicio(audiencia.getDataHoraInicio())
                .dataHoraFim(audiencia.getDataHoraFim())
                .tipo(audiencia.getTipo())
                .local(mapearLocalDto(audiencia.getLocal()))
                .processoId(audiencia.getProcesso().getId())
                .build();
    }

    public static LocalAudiencia mapearLocal(LocalAudienciaDTO dto) {
        LocalAudiencia local = new LocalAudiencia();
        local.setNome(dto.nome());
        local.setRua(dto.rua());
        local.setNumero(dto.numero());
        local.setBairro(dto.bairro());
        local.setCep(dto.cep());
        local.setEstado(dto.estado());
        local.setPais(dto.pais());
        return local;
    }

    private static LocalAudienciaDTO mapearLocalDto(LocalAudiencia entidade) {
        return LocalAudienciaDTO.builder()
                .nome(entidade.getNome())
                .rua(entidade.getRua())
                .numero(entidade.getNumero())
                .bairro(entidade.getBairro())
                .cep(entidade.getCep())
                .estado(entidade.getEstado())
                .pais(entidade.getPais())
                .build();
    }
}
