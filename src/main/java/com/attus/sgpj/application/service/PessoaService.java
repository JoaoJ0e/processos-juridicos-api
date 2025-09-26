package com.attus.sgpj.application.service;

import com.attus.sgpj.domain.dto.PessoaRequestDTO;
import com.attus.sgpj.domain.dto.PessoaResponseDTO;
import com.attus.sgpj.domain.exception.PessoaAlreadyExistsException;
import com.attus.sgpj.domain.exception.PessoaNotFoundException;
import com.attus.sgpj.domain.model.pessoa.Pessoa;
import com.attus.sgpj.domain.repository.PessoaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class PessoaService {

    private final PessoaRepository pessoaRepository;

    public PessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    public PessoaResponseDTO create(PessoaRequestDTO requestDTO) {
        if (pessoaRepository.existsByCpfCnpj(requestDTO.cpfCnpj())) {
            throw new PessoaAlreadyExistsException("CPF/CNPJ já está cadastrado: " + requestDTO.cpfCnpj());
        }

        Pessoa pessoa = Pessoa.create(
                requestDTO.nomeCompleto(),
                requestDTO.cpfCnpj(),
                requestDTO.email(),
                requestDTO.telefone()
        );

        Pessoa savedPessoa = pessoaRepository.save(pessoa);
        return toResponseDTO(savedPessoa);
    }

    public PessoaResponseDTO update(UUID id, PessoaRequestDTO requestDTO) {
        Pessoa pessoa = findPessoaById(id);

        if (!pessoa.getCpfCnpj().equals(requestDTO.cpfCnpj()) &&
                pessoaRepository.existsByCpfCnpj(requestDTO.cpfCnpj())) {
            throw new PessoaAlreadyExistsException("CPF/CNPJ já está cadastrado: " + requestDTO.cpfCnpj());
        }

        pessoa.update(requestDTO);
        Pessoa updatedPessoa = pessoaRepository.save(pessoa);
        return toResponseDTO(updatedPessoa);
    }

    @Transactional(readOnly = true)
    public PessoaResponseDTO findById(UUID id) {
        Pessoa pessoa = findPessoaById(id);
        return toResponseDTO(pessoa);
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
        return pessoaRepository.findAll(pageable).map(this::toResponseDTO);
    }


    @Transactional(readOnly = true)
    public Page<PessoaResponseDTO> findByNomeContaining(String nome, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Pessoa> pessoasPage = pessoaRepository.findByNomeCompletoContainingIgnoreCase(nome, pageable);
        return pessoasPage.map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public PessoaResponseDTO findByCpfCnpj(String cpfCnpj) {
        Pessoa pessoa = pessoaRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new PessoaNotFoundException("Pessoa não encontrada com CPF/CNPJ: " + cpfCnpj));
        return toResponseDTO(pessoa);
    }

    private Pessoa findPessoaById(UUID id) {
        return pessoaRepository.findById(id)
                .orElseThrow(() -> new PessoaNotFoundException("Pessoa não encontrada com ID: " + id));
    }

    private PessoaResponseDTO toResponseDTO(Pessoa pessoa) {
        return new PessoaResponseDTO(
                pessoa.getId(),
                pessoa.getNomeCompleto(),
                pessoa.getCpfCnpj(),
                pessoa.getEmail().getValue(),
                pessoa.getTelefone().getNumero()
        );
    }
}
