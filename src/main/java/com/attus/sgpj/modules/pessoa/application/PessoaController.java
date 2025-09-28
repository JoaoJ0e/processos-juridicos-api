package com.attus.sgpj.modules.pessoa.application;

import com.attus.sgpj.modules.pessoa.domain.dto.PessoaRequestDTO;
import com.attus.sgpj.modules.pessoa.domain.dto.PessoaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/pessoa")
@Tag(name = "Pessoas", description = "Gerenciamento de pessoas (dados, e VOs como email, telefone, etc.)")
public class PessoaController {

    private final PessoaService pessoaService;

    @PostMapping
    @Operation(summary = "Criar pessoa", description = "Cria uma nova pessoa no sistema")
    public ResponseEntity<PessoaResponseDTO> create(@Valid @RequestBody PessoaRequestDTO dto) {
        PessoaResponseDTO response = pessoaService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Retorna os dados de uma pessoa pelo ID")
    public ResponseEntity<PessoaResponseDTO> findById(@PathVariable UUID id) {
        PessoaResponseDTO response = pessoaService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pessoa", description = "Atualiza os dados de uma pessoa existente")
    public ResponseEntity<PessoaResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody PessoaRequestDTO dto) {
        PessoaResponseDTO response = pessoaService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar pessoas paginadas", description = "Lista todas as pessoas com paginação e ordenação")
    public ResponseEntity<Page<PessoaResponseDTO>> findPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        Page<PessoaResponseDTO> response = pessoaService.findPaged(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar por nome", description = "Busca pessoas cujo nome contenha o termo informado")
    public ResponseEntity<Page<PessoaResponseDTO>> findByNome(
            @RequestParam String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PessoaResponseDTO> response = pessoaService.findByNomeContaining(nome, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cpf/{cpfCnpj}")
    @Operation(summary = "Buscar por CPF/CNPJ", description = "Busca uma pessoa pelo CPF ou CNPJ")
    public ResponseEntity<PessoaResponseDTO> findByCpfCnpj(@PathVariable String cpfCnpj) {
        PessoaResponseDTO response = pessoaService.findByCpfCnpj(cpfCnpj);
        return ResponseEntity.ok(response);
    }
}

