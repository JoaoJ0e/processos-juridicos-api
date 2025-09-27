package com.attus.sgpj.modules.pessoa.application;

import com.attus.sgpj.modules.pessoa.domain.dto.PessoaRequestDTO;
import com.attus.sgpj.modules.pessoa.domain.dto.PessoaResponseDTO;
import com.attus.sgpj.modules.pessoa.exception.PessoaAlreadyExistsException;
import com.attus.sgpj.modules.pessoa.exception.PessoaNotFoundException;
import com.attus.sgpj.modules.pessoa.domain.Pessoa;
import com.attus.sgpj.shared.vo.CpfCnpj;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class PessoaService {

    private final PessoaRepository pessoaRepository;

    public PessoaResponseDTO create(PessoaRequestDTO requestDTO) {
        if (pessoaRepository.existsByCpfCnpj(new CpfCnpj(requestDTO.cpfCnpj()))) {
            throw new PessoaAlreadyExistsException("CPF/CNPJ já está cadastrado: " + requestDTO.cpfCnpj());
        }

        Pessoa pessoa = Pessoa.create(
                requestDTO.nomeCompleto(),
                requestDTO.cpfCnpj(),
                requestDTO.email(),
                requestDTO.telefone()
        );

        Pessoa savedPessoa = pessoaRepository.save(pessoa);
        return PessoaResponseDTO.fromDomain(savedPessoa);
    }

    public PessoaResponseDTO update(UUID id, PessoaRequestDTO requestDTO) {
        Pessoa pessoa = findPessoaById(id);

        if (!pessoa.getCpfCnpj().getValue().equals(requestDTO.cpfCnpj()) &&
                pessoaRepository.existsByCpfCnpj(new CpfCnpj(requestDTO.cpfCnpj()))) {
            throw new PessoaAlreadyExistsException("CPF/CNPJ já está cadastrado: " + requestDTO.cpfCnpj());
        }

        pessoa.update(requestDTO);
        Pessoa updatedPessoa = pessoaRepository.save(pessoa);
        return PessoaResponseDTO.fromDomain(updatedPessoa);
    }

    @Transactional(readOnly = true)
    public PessoaResponseDTO findById(UUID id) {
        Pessoa pessoa = findPessoaById(id);
        return PessoaResponseDTO.fromDomain(pessoa);
    }

    @Transactional(readOnly = true)
    public Pessoa findDomainById(UUID id) {
        return findPessoaById(id);
    }

    @Transactional(readOnly = true)
    public Page<PessoaResponseDTO> findPaged(int page, int size, String sortBy, String sortDirection) {
        Pageable pageable;
        if (sortBy == null || sortDirection == null) {
            pageable = PageRequest.of(page, size);
        } else {
            Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
            pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        }
        return pessoaRepository.findAll(pageable).map(PessoaResponseDTO::fromDomain);
    }


    @Transactional(readOnly = true)
    public Page<PessoaResponseDTO> findByNomeContaining(String nome, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Pessoa> pessoasPage = pessoaRepository.findByNomeCompletoContainingIgnoreCase(nome, pageable);
        return pessoasPage.map(PessoaResponseDTO::fromDomain);
    }

    @Transactional(readOnly = true)
    public PessoaResponseDTO findByCpfCnpj(String cpfCnpj) {
        Pessoa pessoa = pessoaRepository.findByCpfCnpj(new CpfCnpj(cpfCnpj))
                .orElseThrow(() -> new PessoaNotFoundException("Pessoa não encontrada com CPF/CNPJ: " + cpfCnpj));
        return PessoaResponseDTO.fromDomain(pessoa);
    }

    private Pessoa findPessoaById(UUID id) {
        return pessoaRepository.findById(id)
                .orElseThrow(() -> new PessoaNotFoundException("Pessoa não encontrada com ID: " + id));
    }

}
