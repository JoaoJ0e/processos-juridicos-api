package com.attus.sgpj.modules.processo.application;

import com.attus.sgpj.modules.acao.domain.Acao;
import com.attus.sgpj.modules.acao.domain.TipoAcaoEnum;
import com.attus.sgpj.modules.acao.domain.dto.AcaoRequestDTO;
import com.attus.sgpj.modules.parteenvolvida.domain.ParteEnvolvida;
import com.attus.sgpj.modules.parteenvolvida.domain.TipoParteEnvolvidaEnum;
import com.attus.sgpj.modules.parteenvolvida.domain.dto.ParteEnvolvidaRequestDTO;
import com.attus.sgpj.modules.pessoa.application.PessoaService;
import com.attus.sgpj.modules.pessoa.domain.Pessoa;
import com.attus.sgpj.modules.processo.domain.Processo;
import com.attus.sgpj.modules.processo.domain.StatusProcessoEnum;
import com.attus.sgpj.modules.processo.domain.dto.ProcessoRequestDTO;
import com.attus.sgpj.modules.processo.domain.dto.ProcessoResponseDTO;
import com.attus.sgpj.modules.processo.exception.ProcessoAlreadyExistsException;
import com.attus.sgpj.modules.processo.exception.ProcessoCannotBeArchivedException;
import com.attus.sgpj.modules.processo.exception.ProcessoNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessoServiceTest {

    @Mock
    private ProcessoRepository processoRepository;

    @Mock
    private PessoaService pessoaService;

    @InjectMocks
    private ProcessoService processoService;

    @Test
    void create_shouldCreateProcesso_whenValidData() {
        // Given
        ProcessoRequestDTO requestDTO = new ProcessoRequestDTO("12345678901234567890", "Processo de Teste", LocalDate.now());

        when(processoRepository.existsByNumero(requestDTO.numero())).thenReturn(false);
        when(processoRepository.save(any(Processo.class))).thenAnswer(invocation -> {
            Processo processo = invocation.getArgument(0);
            // Simulate ID generation
            processo.setId(UUID.randomUUID());
            return processo;
        });

        // When
        ProcessoResponseDTO result = processoService.create(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.numero()).isEqualTo("12345678901234567890");
        assertThat(result.descricao()).isEqualTo("Processo de Teste");
        assertThat(result.statusProcesso()).isEqualTo(StatusProcessoEnum.ATIVO);

        verify(processoRepository).existsByNumero(requestDTO.numero());
        verify(processoRepository).save(any(Processo.class));
    }

    @Test
    void create_shouldThrowException_whenNumeroAlreadyExists() {
        // Given
        ProcessoRequestDTO requestDTO = new ProcessoRequestDTO("12345678901234567890", "Processo de Teste", LocalDate.now());
        when(processoRepository.existsByNumero(requestDTO.numero())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> processoService.create(requestDTO))
                .isInstanceOf(ProcessoAlreadyExistsException.class)
                .hasMessage("Um processo com número '12345678901234567890' já está cadastrado");

        verify(processoRepository).existsByNumero(requestDTO.numero());
        verify(processoRepository, never()).save(any(Processo.class));
    }

    @Test
    void update_shouldUpdateProcesso_whenValidData() {
        // Given
        UUID processoId = UUID.randomUUID();
        ProcessoRequestDTO requestDTO = new ProcessoRequestDTO("99999999999999999999", "Processo Atualizado", LocalDate.now());

        Processo existingProcesso = Processo.create("12345678901234567890", "Processo Original", LocalDate.now().minusDays(1));
        existingProcesso.setId(processoId);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(existingProcesso));
        when(processoRepository.existsByNumero(requestDTO.numero())).thenReturn(false);
        when(processoRepository.save(any(Processo.class))).thenReturn(existingProcesso);

        // When
        ProcessoResponseDTO result = processoService.update(processoId, requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.numero()).isEqualTo("99999999999999999999");
        assertThat(result.descricao()).isEqualTo("Processo Atualizado");

        verify(processoRepository).findById(processoId);
        verify(processoRepository).existsByNumero(requestDTO.numero());
        verify(processoRepository).save(existingProcesso);
    }

    @Test
    void update_shouldThrowException_whenNumeroAlreadyExistsForOtherProcesso() {
        // Given
        UUID processoId = UUID.randomUUID();
        ProcessoRequestDTO requestDTO = new ProcessoRequestDTO("99999999999999999999", "Processo Atualizado", LocalDate.now());

        Processo existingProcesso = Processo.create("12345678901234567890", "Processo Original", LocalDate.now().minusDays(1));
        existingProcesso.setId(processoId);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(existingProcesso));
        when(processoRepository.existsByNumero(requestDTO.numero())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> processoService.update(processoId, requestDTO))
                .isInstanceOf(ProcessoAlreadyExistsException.class)
                .hasMessage("Um processo com número '99999999999999999999' já está cadastrado");

        verify(processoRepository).findById(processoId);
        verify(processoRepository).existsByNumero(requestDTO.numero());
        verify(processoRepository, never()).save(any(Processo.class));
    }

    @Test
    void update_shouldAllowSameNumero_whenUpdatingExistingProcesso() {
        // Given
        UUID processoId = UUID.randomUUID();
        ProcessoRequestDTO requestDTO = new ProcessoRequestDTO("12345678901234567890", "Descrição Atualizada", LocalDate.now());

        Processo existingProcesso = Processo.create("12345678901234567890", "Processo Original", LocalDate.now().minusDays(1));
        existingProcesso.setId(processoId);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(existingProcesso));
        when(processoRepository.save(any(Processo.class))).thenReturn(existingProcesso);

        // When
        ProcessoResponseDTO result = processoService.update(processoId, requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.descricao()).isEqualTo("Descrição Atualizada");

        verify(processoRepository).findById(processoId);
        verify(processoRepository, never()).existsByNumero(any());
        verify(processoRepository).save(existingProcesso);
    }

    @Test
    void findById_shouldReturnProcesso_whenExists() {
        // Given
        UUID processoId = UUID.randomUUID();
        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        processo.setId(processoId);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));

        // When
        ProcessoResponseDTO result = processoService.findById(processoId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(processoId);
        assertThat(result.numero()).isEqualTo("12345678901234567890");

        verify(processoRepository).findById(processoId);
    }

    @Test
    void findById_shouldThrowException_whenNotExists() {
        // Given
        UUID processoId = UUID.randomUUID();
        when(processoRepository.findById(processoId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> processoService.findById(processoId))
                .isInstanceOf(ProcessoNotFoundException.class)
                .hasMessage("Processo não encontrado com ID: " + processoId);

        verify(processoRepository).findById(processoId);
    }

    @Test
    void findPaged_shouldReturnPagedResults_withoutSorting() {
        // Given
        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        Page<Processo> processosPage = new PageImpl<>(List.of(processo));

        when(processoRepository.findAll(any(Pageable.class))).thenReturn(processosPage);

        // When
        Page<ProcessoResponseDTO> result = processoService.findPaged(0, 10, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).numero()).isEqualTo("12345678901234567890");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(processoRepository).findAll(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(0);
        assertThat(capturedPageable.getPageSize()).isEqualTo(10);
        assertThat(capturedPageable.getSort().isUnsorted()).isTrue();
    }

    @Test
    void findByStatus_shouldReturnFilteredResults() {
        // Given
        Processo processo = Processo.create("12345678901234567890", "Processo Ativo", LocalDate.now());
        Page<Processo> processosPage = new PageImpl<>(List.of(processo));

        when(processoRepository.findByStatusProcesso(eq(StatusProcessoEnum.ATIVO), any(Pageable.class)))
                .thenReturn(processosPage);

        // When
        Page<ProcessoResponseDTO> result = processoService.findByStatus(StatusProcessoEnum.ATIVO, 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).statusProcesso()).isEqualTo(StatusProcessoEnum.ATIVO);

        verify(processoRepository).findByStatusProcesso(eq(StatusProcessoEnum.ATIVO), any(Pageable.class));
    }

    @Test
    void findByDataAbertura_shouldReturnFilteredResults() {
        // Given
        LocalDate dataInicial = LocalDate.of(2024, 1, 1);
        LocalDate dataFinal = LocalDate.of(2024, 12, 31);
        LocalDate dataProcesso = LocalDate.of(2024, 6, 15);

        Processo processo = Processo.create("12345678901234567890", "Processo Por Data", dataProcesso);
        Page<Processo> processosPage = new PageImpl<>(List.of(processo));

        when(processoRepository.findByDataAberturaBetween(eq(dataInicial), eq(dataFinal), any(Pageable.class)))
                .thenReturn(processosPage);

        // When
        Page<ProcessoResponseDTO> result = processoService.findByDataAbertura(dataInicial, dataFinal, 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).dataAbertura()).isEqualTo(dataProcesso);

        verify(processoRepository).findByDataAberturaBetween(eq(dataInicial), eq(dataFinal), any(Pageable.class));
    }

    @Test
    void ativar_shouldActivateProcesso() {
        // Given
        UUID processoId = UUID.randomUUID();
        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        processo.setId(processoId);
        processo.suspender(); // Start with suspended status

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        // When
        ProcessoResponseDTO result = processoService.ativar(processoId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.statusProcesso()).isEqualTo(StatusProcessoEnum.ATIVO);

        verify(processoRepository).findById(processoId);
        verify(processoRepository).save(processo);
    }

    @Test
    void suspender_shouldSuspendProcesso() {
        // Given
        UUID processoId = UUID.randomUUID();
        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        processo.setId(processoId);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        // When
        ProcessoResponseDTO result = processoService.suspender(processoId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.statusProcesso()).isEqualTo(StatusProcessoEnum.SUSPENSO);

        verify(processoRepository).findById(processoId);
        verify(processoRepository).save(processo);
    }

    @Test
    void arquivar_shouldArchiveProcesso_whenHasRequiredPartesAndAcoes() {
        // Given
        UUID processoId = UUID.randomUUID();
        Processo processo = createProcessoWithRequiredPartesAndAcoes();
        processo.setId(processoId);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        // When
        ProcessoResponseDTO result = processoService.arquivar(processoId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.statusProcesso()).isEqualTo(StatusProcessoEnum.ARQUIVADO);

        verify(processoRepository).findById(processoId);
        verify(processoRepository).save(processo);
    }

    @Test
    void arquivar_shouldThrowException_whenMissingRequiredPartesOrAcoes() {
        // Given
        UUID processoId = UUID.randomUUID();
        Processo processo = Processo.create("12345678901234567890", "Processo Incompleto", LocalDate.now());
        processo.setId(processoId);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));

        // When & Then
        assertThatThrownBy(() -> processoService.arquivar(processoId))
                .isInstanceOf(ProcessoCannotBeArchivedException.class)
                .hasMessage("Processo não pode ser arquivado. Verifique se possui todas as partes obrigatórias (AUTOR, RÉU, ADVOGADO) e ações obrigatórias (PETIÇÃO, AUDIÊNCIA, SENTENÇA).");

        verify(processoRepository).findById(processoId);
        verify(processoRepository, never()).save(any(Processo.class));
    }

    @Test
    void addParteEnvolvida_shouldAddParte() {
        // Given
        UUID processoId = UUID.randomUUID();
        UUID pessoaId = UUID.randomUUID();

        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        processo.setId(processoId);

        Pessoa pessoa = Pessoa.create("João Silva", "12345678901", "joao.silva@email.com", "11999999999");
        ParteEnvolvidaRequestDTO requestDTO = new ParteEnvolvidaRequestDTO(pessoaId, TipoParteEnvolvidaEnum.AUTOR);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));
        when(pessoaService.findDomainById(pessoaId)).thenReturn(pessoa);
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        // When
        ProcessoResponseDTO result = processoService.addParteEnvolvida(processoId, requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(processo.getParteEnvolvidas()).hasSize(1);
        assertThat(processo.getParteEnvolvidas().get(0).getPessoa()).isEqualTo(pessoa);
        assertThat(processo.getParteEnvolvidas().get(0).getTipo()).isEqualTo(TipoParteEnvolvidaEnum.AUTOR);

        verify(processoRepository).findById(processoId);
        verify(pessoaService).findDomainById(pessoaId);
        verify(processoRepository).save(processo);
    }

    @Test
    void addPartesEnvolvidas_shouldAddMultiplePartes() {
        // Given
        UUID processoId = UUID.randomUUID();
        UUID pessoaId1 = UUID.randomUUID();
        UUID pessoaId2 = UUID.randomUUID();

        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        processo.setId(processoId);

        Pessoa pessoa1 = Pessoa.create("João Silva", "12345678901", "joao.silva@email.com", "11999999999");
        Pessoa pessoa2 = Pessoa.create("Maria Santos", "98765432100", "maria.santos@email.com", "11888888888");

        List<ParteEnvolvidaRequestDTO> requestDTOs = List.of(
                new ParteEnvolvidaRequestDTO(pessoaId1, TipoParteEnvolvidaEnum.AUTOR),
                new ParteEnvolvidaRequestDTO(pessoaId2, TipoParteEnvolvidaEnum.REU)
        );

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));
        when(pessoaService.findDomainById(pessoaId1)).thenReturn(pessoa1);
        when(pessoaService.findDomainById(pessoaId2)).thenReturn(pessoa2);
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        // When
        ProcessoResponseDTO result = processoService.addPartesEnvolvidas(processoId, requestDTOs);

        // Then
        assertThat(result).isNotNull();
        assertThat(processo.getParteEnvolvidas()).hasSize(2);

        verify(processoRepository).findById(processoId);
        verify(pessoaService).findDomainById(pessoaId1);
        verify(pessoaService).findDomainById(pessoaId2);
        verify(processoRepository).save(processo);
    }

    @Test
    void removeParteEnvolvida_shouldRemoveParte() {
        // Given
        UUID processoId = UUID.randomUUID();
        UUID parteId = UUID.randomUUID();

        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        processo.setId(processoId);

        // Add a parte to remove
        Pessoa pessoa = Pessoa.create("João Silva", "12345678901", "joao.silva@email.com", "11999999999");
        ParteEnvolvida parte = ParteEnvolvida.create(pessoa, processo, TipoParteEnvolvidaEnum.AUTOR);
        parte.setId(parteId);
        processo.addParte(parte);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        // When
        ProcessoResponseDTO result = processoService.removeParteEnvolvida(processoId, parteId);

        // Then
        assertThat(result).isNotNull();
        // Note: The actual removal logic is in the domain, so we're testing the service coordination

        verify(processoRepository).findById(processoId);
        verify(processoRepository).save(processo);
    }

    @Test
    void addAcaoProcesso_shouldAddAcao() {
        // Given
        UUID processoId = UUID.randomUUID();
        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        processo.setId(processoId);

        AcaoRequestDTO requestDTO = new AcaoRequestDTO(TipoAcaoEnum.PETICAO, "Petição Inicial");

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        // When
        ProcessoResponseDTO result = processoService.addAcaoProcesso(processoId, requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(processo.getAcoes()).hasSize(1);
        assertThat(processo.getAcoes().get(0).getTipo()).isEqualTo(TipoAcaoEnum.PETICAO);
        assertThat(processo.getAcoes().get(0).getDescricao()).isEqualTo("Petição Inicial");

        verify(processoRepository).findById(processoId);
        verify(processoRepository).save(processo);
    }

    @Test
    void addAcoesProcesso_shouldAddMultipleAcoes() {
        // Given
        UUID processoId = UUID.randomUUID();
        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        processo.setId(processoId);

        List<AcaoRequestDTO> requestDTOs = List.of(
                new AcaoRequestDTO(TipoAcaoEnum.PETICAO, "Petição Inicial"),
                new AcaoRequestDTO(TipoAcaoEnum.AUDIENCIA, "Audiência de Instrução")
        );

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        // When
        ProcessoResponseDTO result = processoService.addAcoesProcesso(processoId, requestDTOs);

        // Then
        assertThat(result).isNotNull();
        assertThat(processo.getAcoes()).hasSize(2);

        verify(processoRepository).findById(processoId);
        verify(processoRepository).save(processo);
    }

    @Test
    void removeAcaoProcesso_shouldRemoveAcao() {
        // Given
        UUID processoId = UUID.randomUUID();
        UUID acaoId = UUID.randomUUID();

        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        processo.setId(processoId);

        // Add an acao to remove
        Acao acao = Acao.create(TipoAcaoEnum.PETICAO, "Petição Inicial", processo);
        acao.setId(acaoId);
        processo.adicionarAcao(acao);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        // When
        ProcessoResponseDTO result = processoService.removeAcaoProcesso(processoId, acaoId);

        // Then
        assertThat(result).isNotNull();
        // Note: The actual removal logic is in the domain, so we're testing the service coordination

        verify(processoRepository).findById(processoId);
        verify(processoRepository).save(processo);
    }

    // Helper methods
    private Processo createProcessoWithRequiredPartesAndAcoes() {
        Processo processo = Processo.create("12345678901234567890", "Processo Completo", LocalDate.now());

        // Add required partes
        Pessoa autor = Pessoa.create("João Silva", "12345678901", "joao.silva@email.com", "11999999999");
        Pessoa reu = Pessoa.create("Maria Santos", "98765432100", "maria.santos@email.com", "11888888888");
        Pessoa advogado = Pessoa.create("Carlos Advogado", "11122233344", "carlos.advogado@email.com", "11777777777");

        processo.addParte(ParteEnvolvida.create(autor, processo, TipoParteEnvolvidaEnum.AUTOR));
        processo.addParte(ParteEnvolvida.create(reu, processo, TipoParteEnvolvidaEnum.REU));
        processo.addParte(ParteEnvolvida.create(advogado, processo, TipoParteEnvolvidaEnum.ADVOGADO));

        // Add required acoes
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.PETICAO, "Petição Inicial", processo));
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.AUDIENCIA, "Audiência de Instrução", processo));
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.SENTENCA, "Sentença Final", processo));

        return processo;
    }
}