package com.incaas.tjrn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incaas.tjrn.dto.request.ProcessoJudicialRequestDTO;
import com.incaas.tjrn.model.StatusProcesso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProcessoJudicialControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return 400 when numeroProcesso has invalid format")
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldReturnBadRequestOnInvalidNumeroProcesso() throws Exception {
        ProcessoJudicialRequestDTO dto = new ProcessoJudicialRequestDTO(
                "123",
                "Vara Cível",
                "Natal",
                "Assunto",
                StatusProcesso.ATIVO.name()
        );

        mockMvc.perform(post("/api/v1/processos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message", containsString("numeroProcesso: Formato inválido")));
    }

    @Test
    @DisplayName("Should create process with valid input. Should return 201")
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldCreateProcessWithValidInput() throws Exception {
        ProcessoJudicialRequestDTO dto = new ProcessoJudicialRequestDTO(
                "1234567-89.2023.1.12.0010",
                "Vara de Família",
                "Mossoró",
                "Divórcio",
                StatusProcesso.ATIVO.name()
        );

        mockMvc.perform(post("/api/v1/processos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroProcesso").value(dto.numeroProcesso()));
    }

    @Test
    @DisplayName("Should return 400 when numeroProcesso already exists")
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldReturnBadRequestWhenNumeroProcessoAlreadyExists() throws Exception {
        // Arrange: First insert a valid processo
        ProcessoJudicialRequestDTO originalDto = new ProcessoJudicialRequestDTO(
                "1234567-89.2023.1.12.0005",
                "Vara Cível",
                "Natal",
                "Assunto original",
                StatusProcesso.ATIVO.name()
        );

        mockMvc.perform(post("/api/v1/processos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(originalDto)))
                .andExpect(status().isCreated());

        // Act: Try to insert a duplicate processo with the same numeroProcesso
        ProcessoJudicialRequestDTO duplicateDto = new ProcessoJudicialRequestDTO(
                "1234567-89.2023.1.12.0005", // same numeroProcesso
                "Vara Criminal",
                "Parnamirim",
                "Outro assunto",
                StatusProcesso.SUSPENSO.name()
        );

        // Assert: Should return 400 with a message about duplication
        mockMvc.perform(post("/api/v1/processos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Já existe um processo com este número")));
    }

}
