package com.attus.sgpj.modules.pessoa.application;

import com.attus.sgpj.modules.pessoa.domain.dto.PessoaRequestDTO;
import com.attus.sgpj.modules.pessoa.domain.dto.PessoaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pessoa criada com sucesso"),
            @ApiResponse(responseCode = "409", description = "Pessoa já existe (PESSOA_ALREADY_EXISTS)"),
            @ApiResponse(responseCode = "409", description = "Campo inválido (CAMPO_INVALIDO)")
    })
    public ResponseEntity<PessoaResponseDTO> create(@Valid @RequestBody PessoaRequestDTO dto) {
        PessoaResponseDTO response = pessoaService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Retorna os dados de uma pessoa pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pessoa encontrada"),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada (PESSOA_NOT_FOUND)")
    })
    public ResponseEntity<PessoaResponseDTO> findById(@PathVariable UUID id) {
        PessoaResponseDTO response = pessoaService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pessoa", description = "Atualiza os dados de uma pessoa existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pessoa atualizada"),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada (PESSOA_NOT_FOUND)"),
            @ApiResponse(responseCode = "409", description = "Campo inválido (CAMPO_INVALIDO)")
    })
    public ResponseEntity<PessoaResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody PessoaRequestDTO dto) {
        PessoaResponseDTO response = pessoaService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar pessoas paginadas", description = "Lista todas as pessoas com paginação e ordenação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<PessoaResponseDTO>> findByNome(
            @RequestParam String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PessoaResponseDTO> response = pessoaService.findByNomeContaining(nome, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cpf/{cpfCnpj}")
    @Operation(summary = "Buscar por CPF/CNPJ", description = "Busca uma pessoa pelo CPF ou CNPJ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pessoa encontrada"),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada (PESSOA_NOT_FOUND)")
    })
    public ResponseEntity<PessoaResponseDTO> findByCpfCnpj(@PathVariable String cpfCnpj) {
        PessoaResponseDTO response = pessoaService.findByCpfCnpj(cpfCnpj);
        return ResponseEntity.ok(response);
    }
}

