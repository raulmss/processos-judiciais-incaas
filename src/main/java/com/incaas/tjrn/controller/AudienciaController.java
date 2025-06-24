package com.incaas.tjrn.controller;

import com.incaas.tjrn.dto.request.AudienciaRequestDTO;
import com.incaas.tjrn.dto.response.AudienciaResponseDTO;
import com.incaas.tjrn.service.AudienciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/audiencias")
@RequiredArgsConstructor
@Tag(name = "Audiências", description = "Gerenciamento e agenda de audiências")
public class AudienciaController {

    private final AudienciaService audienciaService;

    @PostMapping
    @Operation(
            summary = "Agendar nova audiência",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Audiência agendada com sucesso",
                            content = @Content(schema = @Schema(implementation = AudienciaResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Erro de validação ou regra de negócio")
            }
    )
    public AudienciaResponseDTO agendar(
            @RequestBody @Valid AudienciaRequestDTO dto) {
        return audienciaService.agendarAudiencia(dto);
    }

    @GetMapping("/agenda")
    @Operation(
            summary = "Consultar agenda de audiências por comarca e data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de audiências encontrada")
            }
    )
    public Page<AudienciaResponseDTO> consultarAgenda(
            @RequestParam(required = false) String comarca,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Data no formato yyyy-MM-dd") LocalDate data,
            Pageable pageable
    ) {
        return audienciaService.consultarAgenda(comarca, data, pageable);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar uma audiência existente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Audiência atualizada com sucesso",
                            content = @Content(schema = @Schema(implementation = AudienciaResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Audiência não encontrada")
            }
    )
    public AudienciaResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody @Valid AudienciaRequestDTO dto
    ) {
        return audienciaService.atualizarAudiencia(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar uma audiência",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Audiência removida com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Audiência não encontrada")
            }
    )
    public void deletar(@PathVariable Long id) {
        audienciaService.deletarAudiencia(id);
    }
}
