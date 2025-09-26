package com.attus.sgpj.application.controller;

import com.attus.sgpj.application.service.PessoaService;
import com.attus.sgpj.domain.dto.PessoaRequestDTO;
import com.attus.sgpj.domain.dto.PessoaResponseDTO;
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
public class PessoaController {

    private final PessoaService pessoaService;

    @PostMapping
    public ResponseEntity<PessoaResponseDTO> create(@Valid @RequestBody PessoaRequestDTO dto) {
        PessoaResponseDTO response = pessoaService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> findById(@PathVariable UUID id) {
        PessoaResponseDTO response = pessoaService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody PessoaRequestDTO dto) {
        PessoaResponseDTO response = pessoaService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<PessoaResponseDTO>> findPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        Page<PessoaResponseDTO> response = pessoaService.findPaged(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    public ResponseEntity<Page<PessoaResponseDTO>> findByNome(
            @RequestParam String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PessoaResponseDTO> response = pessoaService.findByNomeContaining(nome, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cpf/{cpfCnpj}")
    public ResponseEntity<PessoaResponseDTO> findByCpfCnpj(@PathVariable String cpfCnpj) {
        PessoaResponseDTO response = pessoaService.findByCpfCnpj(cpfCnpj);
        return ResponseEntity.ok(response);
    }


}