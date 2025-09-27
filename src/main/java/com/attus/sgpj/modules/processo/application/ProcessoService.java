package com.attus.sgpj.modules.processo.application;

import com.attus.sgpj.modules.acao.domain.dto.AcaoRequestDTO;
import com.attus.sgpj.modules.pessoa.application.PessoaService;
import com.attus.sgpj.modules.pessoa.domain.Pessoa;
import com.attus.sgpj.modules.pessoa.domain.dto.PessoaResponseDTO;
import com.attus.sgpj.modules.processo.domain.Processo;
import com.attus.sgpj.modules.processo.domain.StatusProcessoEnum;
import com.attus.sgpj.modules.processo.domain.dto.ProcessoRequestDTO;
import com.attus.sgpj.modules.processo.domain.dto.ProcessoResponseDTO;
import com.attus.sgpj.modules.processo.exception.ProcessoAlreadyExistsException;
import com.attus.sgpj.modules.processo.exception.ProcessoNotFoundException;
import com.attus.sgpj.modules.processo.exception.ProcessoCannotBeArchivedException;
import com.attus.sgpj.modules.acao.domain.Acao;
import com.attus.sgpj.modules.parteenvolvida.domain.ParteEnvolvida;
import com.attus.sgpj.modules.parteenvolvida.domain.dto.ParteEnvolvidaRequestDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class ProcessoService {

    private final ProcessoRepository processoRepository;
    private final PessoaService pessoaService;

    public ProcessoResponseDTO create(ProcessoRequestDTO requestDTO) {
        if (processoRepository.existsByNumero(requestDTO.numero())) {
            throw new ProcessoAlreadyExistsException(String.format("Um processo com número '%s' já está cadastrado", requestDTO.numero()));
        }

        Processo processo = Processo.create(requestDTO.numero(), requestDTO.descricao(), requestDTO.dataAbertura());
        Processo savedProcesso = processoRepository.save(processo);
        return ProcessoResponseDTO.fromDomain(savedProcesso);
    }

    public ProcessoResponseDTO update(UUID id, ProcessoRequestDTO requestDTO) {
        Processo processo = findProcessoById(id);

        if (!processo.getNumero().equals(requestDTO.numero()) &&
                processoRepository.existsByNumero(requestDTO.numero())) {
            throw new ProcessoAlreadyExistsException(String.format("Um processo com número '%s' já está cadastrado", requestDTO.numero()));
        }

        processo.update(requestDTO.numero(), requestDTO.descricao(), requestDTO.dataAbertura());
        Processo updatedProcesso = processoRepository.save(processo);
        return ProcessoResponseDTO.fromDomain(updatedProcesso);
    }

    @Transactional(readOnly = true)
    public ProcessoResponseDTO findById(UUID id) {
        Processo processo = findProcessoById(id);
        return ProcessoResponseDTO.fromDomain(processo);
    }

    @Transactional(readOnly = true)
    public Page<ProcessoResponseDTO> findPaged(int page, int size, String sortBy, String sortDirection) {
        Pageable pageable;
        if (sortBy == null || sortDirection == null) {
            pageable = PageRequest.of(page, size);
        } else {
            Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
            pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        }
        return processoRepository.findAll(pageable).map(ProcessoResponseDTO::fromDomain);
    }

    @Transactional(readOnly = true)
    public Page<ProcessoResponseDTO> findByStatus(StatusProcessoEnum status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Processo> processosPage = processoRepository.findByStatusProcesso(status, pageable);
        return processosPage.map(ProcessoResponseDTO::fromDomain);
    }

    @Transactional(readOnly = true)
    public Page<ProcessoResponseDTO> findByDataAbertura(LocalDate dataInicial, LocalDate dataFinal, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Processo> processosPage = processoRepository.findByDataAberturaBetween(dataInicial, dataFinal, pageable);
        return processosPage.map(ProcessoResponseDTO::fromDomain);
    }

    @Transactional(readOnly = true)
    public Page<ProcessoResponseDTO> findByCpfCnpjParteEnvolvida(String cpfCnpj, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Processo> processosPage = processoRepository.findByParteEnvolvidaCpfCnpj(cpfCnpj, pageable);
        return processosPage.map(ProcessoResponseDTO::fromDomain);
    }

    @Transactional(readOnly = true)
    public Page<ProcessoResponseDTO> findByPessoaId(UUID pessoaId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Processo> processosPage = processoRepository.findByPessoaId(pessoaId, pageable);
        return processosPage.map(ProcessoResponseDTO::fromDomain);
    }

    @Transactional(readOnly = true)
    public Page<ProcessoResponseDTO> findByPessoaCpfCnpj(String cpfCnpj, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Processo> processosPage = processoRepository.findByParteEnvolvidaCpfCnpj(cpfCnpj, pageable);
        return processosPage.map(ProcessoResponseDTO::fromDomain);
    }

    // Business operations
    public ProcessoResponseDTO ativar(UUID processoId) {
        Processo processo = findProcessoById(processoId);
        processo.ativar();
        Processo updatedProcesso = processoRepository.save(processo);
        return ProcessoResponseDTO.fromDomain(updatedProcesso);
    }

    public ProcessoResponseDTO suspender(UUID processoId) {
        Processo processo = findProcessoById(processoId);
        processo.suspender();
        Processo updatedProcesso = processoRepository.save(processo);
        return ProcessoResponseDTO.fromDomain(updatedProcesso);
    }

    public ProcessoResponseDTO arquivar(UUID processoId) {
        Processo processo = findProcessoById(processoId);

        if (!processo.podeArquivar()) {
            throw new ProcessoCannotBeArchivedException("Processo não pode ser arquivado. Verifique se possui todas as partes obrigatórias (AUTOR, RÉU, ADVOGADO) e ações obrigatórias (PETIÇÃO, AUDIÊNCIA, SENTENÇA).");
        }

        processo.arquivar();
        Processo updatedProcesso = processoRepository.save(processo);
        return ProcessoResponseDTO.fromDomain(updatedProcesso);
    }

    // Parte Envolvida operations
    public ProcessoResponseDTO addParteEnvolvida(UUID processoId, ParteEnvolvidaRequestDTO requestDTO) {
        Processo processo = findProcessoById(processoId);
        Pessoa pessoa = pessoaService.findDomainById(requestDTO.pessoaId());
        ParteEnvolvida parte = ParteEnvolvida.create(pessoa, processo, requestDTO.tipo());

        processo.addParte(parte);
        Processo updatedProcesso = processoRepository.save(processo);
        return ProcessoResponseDTO.fromDomain(updatedProcesso);
    }

    public ProcessoResponseDTO addPartesEnvolvidas(UUID processoId, List<ParteEnvolvidaRequestDTO> requestDTOs) {
        Processo processo = findProcessoById(processoId);

        for (ParteEnvolvidaRequestDTO requestDTO : requestDTOs) {
            Pessoa pessoa = pessoaService.findDomainById(requestDTO.pessoaId());
            ParteEnvolvida parte = ParteEnvolvida.create(pessoa, processo, requestDTO.tipo());
            processo.addParte(parte);
        }

        Processo updatedProcesso = processoRepository.save(processo);
        return ProcessoResponseDTO.fromDomain(updatedProcesso);
    }

    public ProcessoResponseDTO removeParteEnvolvida(UUID processoId, UUID parteEnvolvidaId) {
        Processo processo = findProcessoById(processoId);
        processo.removeParteById(parteEnvolvidaId);
        Processo updatedProcesso = processoRepository.save(processo);
        return ProcessoResponseDTO.fromDomain(updatedProcesso);
    }

    // Ação operations
    public ProcessoResponseDTO addAcaoProcesso(UUID processoId, AcaoRequestDTO requestDTO) {
        Processo processo = findProcessoById(processoId);
        Acao acao = Acao.create(requestDTO.tipo(), requestDTO.descricao(), processo);
        processo.adicionarAcao(acao);
        Processo updatedProcesso = processoRepository.save(processo);
        return ProcessoResponseDTO.fromDomain(updatedProcesso);
    }

    public ProcessoResponseDTO addAcoesProcesso(UUID processoId, List<AcaoRequestDTO> requestDTOs) {
        Processo processo = findProcessoById(processoId);

        for (AcaoRequestDTO requestDTO : requestDTOs) {
            Acao acao = Acao.create(requestDTO.tipo(), requestDTO.descricao(), processo);
            processo.adicionarAcao(acao);
        }

        Processo updatedProcesso = processoRepository.save(processo);
        return ProcessoResponseDTO.fromDomain(updatedProcesso);
    }

    public ProcessoResponseDTO removeAcaoProcesso(UUID processoId, UUID acaoId) {
        Processo processo = findProcessoById(processoId);
        processo.removeAcaoById(acaoId);
        Processo updatedProcesso = processoRepository.save(processo);
        return ProcessoResponseDTO.fromDomain(updatedProcesso);
    }

    private Processo findProcessoById(UUID id) {
        return processoRepository.findById(id)
                .orElseThrow(() -> new ProcessoNotFoundException("Processo não encontrado com ID: " + id));
    }
}