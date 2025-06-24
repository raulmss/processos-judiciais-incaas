
package com.incaas.tjrn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incaas.tjrn.dto.request.AudienciaRequestDTO;
import com.incaas.tjrn.dto.request.ProcessoJudicialRequestDTO;
import com.incaas.tjrn.dto.shared.LocalAudienciaDTO;
import com.incaas.tjrn.model.ProcessoJudicial;
import com.incaas.tjrn.model.StatusProcesso;
import com.incaas.tjrn.repository.AudienciaRepository;
import com.incaas.tjrn.repository.ProcessoJudicialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AudienciaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProcessoJudicialRepository processoJudicialRepository;

    @Autowired
    private AudienciaRepository audienciaRepository;

    private Long processoId;

    @BeforeEach
    void setup() {
        audienciaRepository.deleteAll();
        processoJudicialRepository.deleteAll();

        ProcessoJudicial processo = new ProcessoJudicial();
        processo.setNumeroProcesso("1234567-89.2023.1.12.0001");
        processo.setVara("Vara Cível");
        processo.setComarca("Natal");
        processo.setAssunto("Assunto");
        processo.setStatus(StatusProcesso.ATIVO);

        processoId = processoJudicialRepository.save(processo).getId();
    }

    private AudienciaRequestDTO createValidAudienciaDTO() {
        return new AudienciaRequestDTO(
                Instant.parse("2025-07-01T14:00:00Z"),
                Instant.parse("2025-07-01T15:00:00Z"),
                "INSTRUCAO",
                new LocalAudienciaDTO("Sala A", "Rua B", "123", "Centro", "59000-000", "RN", "Brasil"),
                processoId
        );
    }

    @Test
    @DisplayName("1. Should schedule a valid audiencia")
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldScheduleValidAudiencia() throws Exception {
        mockMvc.perform(post("/api/v1/audiencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidAudienciaDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.local.nome").value("Sala A"));
    }

    @Test
    @DisplayName("2. Should return 400 for invalid date (weekend)")
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldReturnBadRequestForWeekend() throws Exception {
        var dto = createValidAudienciaDTO();
        dto = new AudienciaRequestDTO(
                Instant.parse("2025-07-06T14:00:00Z"), // Sunday
                Instant.parse("2025-07-06T15:00:00Z"),
                dto.tipo(),
                dto.local(),
                dto.processoId()
        );

        mockMvc.perform(post("/api/v1/audiencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Audiência só pode ser agendada em dias úteis"));
    }

    @Test
    @DisplayName("3. Should return 400 for time conflict")
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldReturnConflictError() throws Exception {
        mockMvc.perform(post("/api/v1/audiencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidAudienciaDTO())))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/audiencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidAudienciaDTO())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Conflito: já existe uma audiência nesse intervalo"));
    }

    @Test
    @DisplayName("4. Should return 400 for missing required fields in LocalAudienciaDTO")
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldReturnValidationErrorForMissingFields() throws Exception {
        var dto = new AudienciaRequestDTO(
                Instant.parse("2025-07-01T14:00:00Z"),
                Instant.parse("2025-07-01T15:00:00Z"),
                "INSTRUCAO",
                new LocalAudienciaDTO("", "", "", "", "", "", ""),
                processoId
        );

        mockMvc.perform(post("/api/v1/audiencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", org.hamcrest.Matchers.containsString("é obrigatório")));
    }

    @Test
    @DisplayName("5. Should return 404 when updating non-existing audiencia")
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldReturnNotFoundOnUpdate() throws Exception {
        var dto = createValidAudienciaDTO();

        mockMvc.perform(put("/api/v1/audiencias/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Audiência não encontrada com ID: 9999"));
    }

    @Test
    @DisplayName("6. Should return 404 when deleting non-existing audiencia")
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void shouldReturnNotFoundOnDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/audiencias/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Audiência não encontrada com ID: 9999"));
    }

    @Test
    @DisplayName("7. Should list scheduled audiencias by comarca and date")
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldListAudienciasByDateAndComarca() throws Exception {
        mockMvc.perform(post("/api/v1/audiencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidAudienciaDTO())))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/audiencias/agenda")
                        .param("comarca", "Natal")
                        .param("data", "2025-07-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].local.nome").value("Sala A"));
    }
}