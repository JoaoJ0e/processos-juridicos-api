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
    void deveCriarProcessoComDadosValidos() {
        ProcessoRequestDTO requestDTO = new ProcessoRequestDTO("12345678901234567890", "Processo de Teste", LocalDate.now());

        when(processoRepository.existsByNumero(requestDTO.numero())).thenReturn(false);
        when(processoRepository.save(any(Processo.class))).thenAnswer(invocation -> {
            Processo processo = invocation.getArgument(0);
            processo.setId(UUID.randomUUID());
            return processo;
        });

        ProcessoResponseDTO result = processoService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.numero()).isEqualTo("12345678901234567890");
        assertThat(result.descricao()).isEqualTo("Processo de Teste");
        assertThat(result.statusProcesso()).isEqualTo(StatusProcessoEnum.ATIVO);

        verify(processoRepository).existsByNumero(requestDTO.numero());
        verify(processoRepository).save(any(Processo.class));
    }

    @Test
    void deveLancarExcecaoQuandoNumeroJaExiste() {
        ProcessoRequestDTO requestDTO = new ProcessoRequestDTO("12345678901234567890", "Processo de Teste", LocalDate.now());
        when(processoRepository.existsByNumero(requestDTO.numero())).thenReturn(true);

        assertThatThrownBy(() -> processoService.create(requestDTO))
                .isInstanceOf(ProcessoAlreadyExistsException.class)
                .hasMessage("Um processo com número '12345678901234567890' já está cadastrado");

        verify(processoRepository).existsByNumero(requestDTO.numero());
        verify(processoRepository, never()).save(any(Processo.class));
    }

    @Test
    void deveAtualizarProcessoComDadosValidos() {
        UUID processoId = UUID.randomUUID();
        ProcessoRequestDTO requestDTO = new ProcessoRequestDTO("99999999999999999999", "Processo Atualizado", LocalDate.now());

        Processo existingProcesso = Processo.create("12345678901234567890", "Processo Original", LocalDate.now().minusDays(1));
        existingProcesso.setId(processoId);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(existingProcesso));
        when(processoRepository.existsByNumero(requestDTO.numero())).thenReturn(false);
        when(processoRepository.save(any(Processo.class))).thenReturn(existingProcesso);

        ProcessoResponseDTO result = processoService.update(processoId, requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.numero()).isEqualTo("99999999999999999999");
        assertThat(result.descricao()).isEqualTo("Processo Atualizado");

        verify(processoRepository).findById(processoId);
        verify(processoRepository).existsByNumero(requestDTO.numero());
        verify(processoRepository).save(existingProcesso);
    }

    @Test
    void deveLancarExcecaoQuandoNumeroJaExisteParaOutroProcesso() {
        UUID processoId = UUID.randomUUID();
        ProcessoRequestDTO requestDTO = new ProcessoRequestDTO("99999999999999999999", "Processo Atualizado", LocalDate.now());

        Processo existingProcesso = Processo.create("12345678901234567890", "Processo Original", LocalDate.now().minusDays(1));
        existingProcesso.setId(processoId);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(existingProcesso));
        when(processoRepository.existsByNumero(requestDTO.numero())).thenReturn(true);

        assertThatThrownBy(() -> processoService.update(processoId, requestDTO))
                .isInstanceOf(ProcessoAlreadyExistsException.class)
                .hasMessage("Um processo com número '99999999999999999999' já está cadastrado");

        verify(processoRepository).findById(processoId);
        verify(processoRepository).existsByNumero(requestDTO.numero());
        verify(processoRepository, never()).save(any(Processo.class));
    }

    @Test
    void devePermitirMesmoNumeroQuandoAtualizandoProcessoExistente() {
        UUID processoId = UUID.randomUUID();
        ProcessoRequestDTO requestDTO = new ProcessoRequestDTO("12345678901234567890", "Descrição Atualizada", LocalDate.now());

        Processo existingProcesso = Processo.create("12345678901234567890", "Processo Original", LocalDate.now().minusDays(1));
        existingProcesso.setId(processoId);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(existingProcesso));
        when(processoRepository.save(any(Processo.class))).thenReturn(existingProcesso);

        ProcessoResponseDTO result = processoService.update(processoId, requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.descricao()).isEqualTo("Descrição Atualizada");

        verify(processoRepository).findById(processoId);
        verify(processoRepository, never()).existsByNumero(any());
        verify(processoRepository).save(existingProcesso);
    }

    @Test
    void deveRetornarProcessoQuandoExiste() {
        UUID processoId = UUID.randomUUID();
        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        processo.setId(processoId);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));

        ProcessoResponseDTO result = processoService.findById(processoId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(processoId);
        assertThat(result.numero()).isEqualTo("12345678901234567890");

        verify(processoRepository).findById(processoId);
    }

    @Test
    void deveLancarExcecaoQuandoProcessoNaoExiste() {
        UUID processoId = UUID.randomUUID();
        when(processoRepository.findById(processoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> processoService.findById(processoId))
                .isInstanceOf(ProcessoNotFoundException.class)
                .hasMessage("Processo não encontrado com ID: " + processoId);

        verify(processoRepository).findById(processoId);
    }

    @Test
    void deveRetornarResultadosPaginadosSemOrdenacao() {
        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        Page<Processo> processosPage = new PageImpl<>(List.of(processo));

        when(processoRepository.findAll(any(Pageable.class))).thenReturn(processosPage);

        Page<ProcessoResponseDTO> result = processoService.findPaged(0, 10, null, null);

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
    void deveRetornarResultadosFiltradosPorStatus() {
        Processo processo = Processo.create("12345678901234567890", "Processo Ativo", LocalDate.now());
        Page<Processo> processosPage = new PageImpl<>(List.of(processo));

        when(processoRepository.findByStatusProcesso(eq(StatusProcessoEnum.ATIVO), any(Pageable.class)))
                .thenReturn(processosPage);

        Page<ProcessoResponseDTO> result = processoService.findByStatus(StatusProcessoEnum.ATIVO, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).statusProcesso()).isEqualTo(StatusProcessoEnum.ATIVO);

        verify(processoRepository).findByStatusProcesso(eq(StatusProcessoEnum.ATIVO), any(Pageable.class));
    }

    @Test
    void deveRetornarResultadosFiltradosPorDataAbertura() {
        LocalDate dataInicial = LocalDate.of(2024, 1, 1);
        LocalDate dataFinal = LocalDate.of(2024, 12, 31);
        LocalDate dataProcesso = LocalDate.of(2024, 6, 15);

        Processo processo = Processo.create("12345678901234567890", "Processo Por Data", dataProcesso);
        Page<Processo> processosPage = new PageImpl<>(List.of(processo));

        when(processoRepository.findByDataAberturaBetween(eq(dataInicial), eq(dataFinal), any(Pageable.class)))
                .thenReturn(processosPage);

        Page<ProcessoResponseDTO> result = processoService.findByDataAbertura(dataInicial, dataFinal, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).dataAbertura()).isEqualTo(dataProcesso);

        verify(processoRepository).findByDataAberturaBetween(eq(dataInicial), eq(dataFinal), any(Pageable.class));
    }

    @Test
    void deveAtivarProcesso() {
        UUID processoId = UUID.randomUUID();
        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        processo.setId(processoId);
        processo.suspender();

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        ProcessoResponseDTO result = processoService.ativar(processoId);

        assertThat(result).isNotNull();
        assertThat(result.statusProcesso()).isEqualTo(StatusProcessoEnum.ATIVO);

        verify(processoRepository).findById(processoId);
        verify(processoRepository).save(processo);
    }

    @Test
    void deveSuspenderProcesso() {
        UUID processoId = UUID.randomUUID();
        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        processo.setId(processoId);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        ProcessoResponseDTO result = processoService.suspender(processoId);

        assertThat(result).isNotNull();
        assertThat(result.statusProcesso()).isEqualTo(StatusProcessoEnum.SUSPENSO);

        verify(processoRepository).findById(processoId);
        verify(processoRepository).save(processo);
    }

    @Test
    void deveArquivarProcessoQuandoTemPartesEAcoesObrigatorias() {
        UUID processoId = UUID.randomUUID();
        Processo processo = createProcessoWithRequiredPartesAndAcoes();
        processo.setId(processoId);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        ProcessoResponseDTO result = processoService.arquivar(processoId);

        assertThat(result).isNotNull();
        assertThat(result.statusProcesso()).isEqualTo(StatusProcessoEnum.ARQUIVADO);

        verify(processoRepository).findById(processoId);
        verify(processoRepository).save(processo);
    }

    @Test
    void deveLancarExcecaoQuandoFaltamPartesOuAcoesObrigatorias() {
        UUID processoId = UUID.randomUUID();
        Processo processo = Processo.create("12345678901234567890", "Processo Incompleto", LocalDate.now());
        processo.setId(processoId);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));

        assertThatThrownBy(() -> processoService.arquivar(processoId))
                .isInstanceOf(ProcessoCannotBeArchivedException.class)
                .hasMessage("Processo não pode ser arquivado. Verifique se possui todas as partes obrigatórias (AUTOR, RÉU, ADVOGADO) e ações obrigatórias (PETIÇÃO, AUDIÊNCIA, SENTENÇA).");

        verify(processoRepository).findById(processoId);
        verify(processoRepository, never()).save(any(Processo.class));
    }

    @Test
    void deveAdicionarParteEnvolvida() {
        UUID processoId = UUID.randomUUID();
        UUID pessoaId = UUID.randomUUID();

        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        processo.setId(processoId);

        Pessoa pessoa = Pessoa.create("João Silva", "12345678901", "joao.silva@email.com", "11999999999");
        ParteEnvolvidaRequestDTO requestDTO = new ParteEnvolvidaRequestDTO(pessoaId, TipoParteEnvolvidaEnum.AUTOR);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));
        when(pessoaService.findDomainById(pessoaId)).thenReturn(pessoa);
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        ProcessoResponseDTO result = processoService.addParteEnvolvida(processoId, requestDTO);

        assertThat(result).isNotNull();
        assertThat(processo.getParteEnvolvidas()).hasSize(1);
        assertThat(processo.getParteEnvolvidas().get(0).getPessoa()).isEqualTo(pessoa);
        assertThat(processo.getParteEnvolvidas().get(0).getTipo()).isEqualTo(TipoParteEnvolvidaEnum.AUTOR);

        verify(processoRepository).findById(processoId);
        verify(pessoaService).findDomainById(pessoaId);
        verify(processoRepository).save(processo);
    }

    @Test
    void deveAdicionarMultiplasPartesEnvolvidas() {
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

        ProcessoResponseDTO result = processoService.addPartesEnvolvidas(processoId, requestDTOs);

        assertThat(result).isNotNull();
        assertThat(processo.getParteEnvolvidas()).hasSize(2);

        verify(processoRepository).findById(processoId);
        verify(pessoaService).findDomainById(pessoaId1);
        verify(pessoaService).findDomainById(pessoaId2);
        verify(processoRepository).save(processo);
    }

    @Test
    void deveRemoverParteEnvolvida() {
        UUID processoId = UUID.randomUUID();
        UUID parteId = UUID.randomUUID();

        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        processo.setId(processoId);

        Pessoa pessoa = Pessoa.create("João Silva", "12345678901", "joao.silva@email.com", "11999999999");
        ParteEnvolvida parte = ParteEnvolvida.create(pessoa, processo, TipoParteEnvolvidaEnum.AUTOR);
        parte.setId(parteId);
        processo.addParte(parte);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        ProcessoResponseDTO result = processoService.removeParteEnvolvida(processoId, parteId);

        assertThat(result).isNotNull();

        verify(processoRepository).findById(processoId);
        verify(processoRepository).save(processo);
    }

    @Test
    void deveAdicionarAcaoProcesso() {
        UUID processoId = UUID.randomUUID();
        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        processo.setId(processoId);

        AcaoRequestDTO requestDTO = new AcaoRequestDTO(TipoAcaoEnum.PETICAO, "Petição Inicial");

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        ProcessoResponseDTO result = processoService.addAcaoProcesso(processoId, requestDTO);

        assertThat(result).isNotNull();
        assertThat(processo.getAcoes()).hasSize(1);
        assertThat(processo.getAcoes().get(0).getTipo()).isEqualTo(TipoAcaoEnum.PETICAO);
        assertThat(processo.getAcoes().get(0).getDescricao()).isEqualTo("Petição Inicial");

        verify(processoRepository).findById(processoId);
        verify(processoRepository).save(processo);
    }

    @Test
    void deveAdicionarMultiplasAcoesProcesso() {
        UUID processoId = UUID.randomUUID();
        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        processo.setId(processoId);

        List<AcaoRequestDTO> requestDTOs = List.of(
                new AcaoRequestDTO(TipoAcaoEnum.PETICAO, "Petição Inicial"),
                new AcaoRequestDTO(TipoAcaoEnum.AUDIENCIA, "Audiência de Instrução")
        );

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        ProcessoResponseDTO result = processoService.addAcoesProcesso(processoId, requestDTOs);

        assertThat(result).isNotNull();
        assertThat(processo.getAcoes()).hasSize(2);

        verify(processoRepository).findById(processoId);
        verify(processoRepository).save(processo);
    }

    @Test
    void deveRemoverAcaoProcesso() {
        UUID processoId = UUID.randomUUID();
        UUID acaoId = UUID.randomUUID();

        Processo processo = Processo.create("12345678901234567890", "Processo Teste", LocalDate.now());
        processo.setId(processoId);

        Acao acao = Acao.create(TipoAcaoEnum.PETICAO, "Petição Inicial", processo);
        acao.setId(acaoId);
        processo.adicionarAcao(acao);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processo));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        ProcessoResponseDTO result = processoService.removeAcaoProcesso(processoId, acaoId);

        assertThat(result).isNotNull();

        verify(processoRepository).findById(processoId);
        verify(processoRepository).save(processo);
    }

    private Processo createProcessoWithRequiredPartesAndAcoes() {
        Processo processo = Processo.create("12345678901234567890", "Processo Completo", LocalDate.now());

        Pessoa autor = Pessoa.create("João Silva", "12345678901", "joao.silva@email.com", "11999999999");
        Pessoa reu = Pessoa.create("Maria Santos", "98765432100", "maria.santos@email.com", "11888888888");
        Pessoa advogado = Pessoa.create("Carlos Advogado", "11122233344", "carlos.advogado@email.com", "11777777777");

        processo.addParte(ParteEnvolvida.create(autor, processo, TipoParteEnvolvidaEnum.AUTOR));
        processo.addParte(ParteEnvolvida.create(reu, processo, TipoParteEnvolvidaEnum.REU));
        processo.addParte(ParteEnvolvida.create(advogado, processo, TipoParteEnvolvidaEnum.ADVOGADO));

        processo.adicionarAcao(Acao.create(TipoAcaoEnum.PETICAO, "Petição Inicial", processo));
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.AUDIENCIA, "Audiência de Instrução", processo));
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.SENTENCA, "Sentença Final", processo));

        return processo;
    }
}