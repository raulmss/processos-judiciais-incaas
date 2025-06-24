package com.incaas.tjrn.controller;

import com.incaas.tjrn.dto.request.ProcessoJudicialRequestDTO;
import com.incaas.tjrn.dto.response.ProcessoJudicialResponseDTO;
import com.incaas.tjrn.model.StatusProcesso;
import com.incaas.tjrn.service.ProcessoJudicialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/processos")
@Tag(name = "Processos Judiciais", description = "Gerenciamento de processos judiciais e seus filtros")
@Validated
public class ProcessoJudicialController {

    @Autowired
    private ProcessoJudicialService service;

    @PostMapping
    @Operation(
            summary = "Criar um novo processo judicial",
            description = "Cadastra um novo processo com validação de número único e campos obrigatórios",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Processo criado com sucesso",
                            content = @Content(schema = @Schema(implementation = ProcessoJudicialResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Erro de validação ou de regra de negócio")
            }
    )
    public ResponseEntity<ProcessoJudicialResponseDTO> criarNovoProcessoJudicial(
            @RequestBody @Valid ProcessoJudicialRequestDTO dto) {
        ProcessoJudicialResponseDTO response = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
            summary = "Listar processos judiciais com filtros opcionais",
            description = "Lista os processos com paginação, podendo filtrar por status e/ou comarca",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de processos retornada com sucesso")
            }
    )
    public Page<ProcessoJudicialResponseDTO> listarProcessosJudiciaisFilter(
            @Parameter(description = "Status do processo para filtro (ATIVO, ARQUIVADO, SUSPENSO)", example = "ATIVO")
            @RequestParam(required = false) StatusProcesso status,

            @Parameter(description = "Comarca para filtro (parcial ou completo)", example = "Natal")
            @RequestParam(required = false) String comarca,

            @Parameter(hidden = true)
            Pageable pageable
    ) {
        return service.findByFilter(status, comarca, pageable);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar um processo existente",
            description = "Atualiza os dados de um processo judicial pelo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Processo atualizado com sucesso",
                            content = @Content(schema = @Schema(implementation = ProcessoJudicialResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Processo não encontrado"),
                    @ApiResponse(responseCode = "400", description = "Erro de validação ou de regra de negócio")
            }
    )
    public ProcessoJudicialResponseDTO updateProcessoJudicial(
            @Parameter(description = "ID do processo a ser atualizado", example = "1")
            @PathVariable Long id,

            @RequestBody @Valid ProcessoJudicialRequestDTO dto
    ) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Excluir um processo judicial",
            description = "Remove um processo judicial pelo ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Processo excluído com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Processo não encontrado")
            }
    )

    @PreAuthorize("hasRole('ADMIN')")
    public void deletar(
            @Parameter(description = "ID do processo a ser deletado", example = "1")
            @PathVariable Long id
    ) {
        service.delete(id);
    }
}
