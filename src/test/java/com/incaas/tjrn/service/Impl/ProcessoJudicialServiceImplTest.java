
package com.incaas.tjrn.service;

import com.incaas.tjrn.dto.request.ProcessoJudicialRequestDTO;
import com.incaas.tjrn.dto.response.ProcessoJudicialResponseDTO;
import com.incaas.tjrn.exception.exceptions.ProcessoJudicialComNumeroJaExisteException;
import com.incaas.tjrn.exception.exceptions.RecursoNaoEncontradoException;
import com.incaas.tjrn.exception.exceptions.ViolacaoDeRegraNegocioException;
import com.incaas.tjrn.model.ProcessoJudicial;
import com.incaas.tjrn.model.StatusProcesso;
import com.incaas.tjrn.repository.ProcessoJudicialRepository;
import com.incaas.tjrn.service.Impl.ProcessoJudicialServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProcessoJudicialServiceImplTest {

    @InjectMocks
    private ProcessoJudicialServiceImpl service;

    @Mock
    private ProcessoJudicialRepository repository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateProcessoSuccessfully() {
        ProcessoJudicialRequestDTO dto = new ProcessoJudicialRequestDTO("1234567-89.2023.1.12.0004", "Vara Cível", "Natal", "Assunto", "ATIVO");

        when(repository.existsByNumeroProcesso("1234567-89.2023.1.12.0004")).thenReturn(false);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        ProcessoJudicialResponseDTO response = service.create(dto);

        assertEquals("1234567-89.2023.1.12.0004", response.numeroProcesso());
        verify(repository, times(1)).save(any());
    }

    @Test
    void shouldThrowWhenCreatingProcessoWithDuplicateNumber() {
        ProcessoJudicialRequestDTO dto = new ProcessoJudicialRequestDTO("1234567-89.2023.1.12.0004", "Vara", "Comarca", "Assunto", "ATIVO");

        when(repository.existsByNumeroProcesso("1234567-89.2023.1.12.0004")).thenReturn(true);

        assertThrows(ProcessoJudicialComNumeroJaExisteException.class, () -> service.create(dto));
    }

    @Test
    void shouldReturnFilteredPageOfProcessos() {
        Pageable pageable = PageRequest.of(0, 10);
        ProcessoJudicial processo = new ProcessoJudicial();
        processo.setNumeroProcesso("1234567-89.2023.1.12.0004");
        Page<ProcessoJudicial> page = new PageImpl<>(List.of(processo));

        when(repository.filtrar(StatusProcesso.ATIVO, "Natal", pageable)).thenReturn(page);

        Page<ProcessoJudicialResponseDTO> result = service.findByFilter(StatusProcesso.ATIVO, "Natal", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("1234567-89.2023.1.12.0004", result.getContent().get(0).numeroProcesso());
    }

    @Test
    void shouldUpdateProcessoSuccessfully() {
        ProcessoJudicialRequestDTO dto = new ProcessoJudicialRequestDTO("1234567-89.2023.1.12.0004", "Vara", "Comarca", "Assunto", "SUSPENSO");

        ProcessoJudicial existing = new ProcessoJudicial();
        existing.setId(1L);
        existing.setNumeroProcesso("1234567-89.2023.1.12.0004");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        ProcessoJudicialResponseDTO response = service.update(1L, dto);

        assertEquals("1234567-89.2023.1.12.0004", response.numeroProcesso());
        assertEquals(StatusProcesso.SUSPENSO, response.status());
    }

    @Test
    void shouldThrowWhenUpdatingNonexistentProcesso() {
        ProcessoJudicialRequestDTO dto = new ProcessoJudicialRequestDTO("1234567-89.2023.1.12.0004", "Vara", "Comarca", "Assunto", "ATIVO");

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.update(1L, dto));
    }

    @Test
    void shouldThrowWhenTryingToChangeProcessNumber() {
        ProcessoJudicialRequestDTO dto = new ProcessoJudicialRequestDTO("1234567-89.2023.1.12.0004", "Vara", "Comarca", "Assunto", "ATIVO");

        ProcessoJudicial existing = new ProcessoJudicial();
        existing.setNumeroProcesso("1234567-89.2023.1.12.0005");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(ViolacaoDeRegraNegocioException.class, () -> service.update(1L, dto));
    }

    @Test
    void shouldDeleteProcessoSuccessfully() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonexistentProcesso() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> service.delete(1L));
    }
}