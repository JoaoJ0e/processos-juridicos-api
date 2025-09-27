package com.attus.sgpj.modules.processo.infrastructure.controller;

import com.attus.sgpj.modules.acao.domain.dto.AcaoRequestDTO;
import com.attus.sgpj.modules.processo.application.ProcessoService;
import com.attus.sgpj.modules.processo.domain.StatusProcessoEnum;
import com.attus.sgpj.modules.processo.domain.dto.ProcessoRequestDTO;
import com.attus.sgpj.modules.processo.domain.dto.ProcessoResponseDTO;
import com.attus.sgpj.modules.parteenvolvida.domain.dto.ParteEnvolvidaRequestDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/processo")
@AllArgsConstructor
public class ProcessoController {

    private final ProcessoService processoService;

    @PostMapping
    public ResponseEntity<ProcessoResponseDTO> create(@Valid @RequestBody ProcessoRequestDTO requestDTO) {
        ProcessoResponseDTO responseDTO = processoService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcessoResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProcessoRequestDTO requestDTO) {
        ProcessoResponseDTO responseDTO = processoService.update(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcessoResponseDTO> findById(@PathVariable UUID id) {
        ProcessoResponseDTO responseDTO = processoService.findById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<ProcessoResponseDTO>> findPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        Page<ProcessoResponseDTO> processosPage = processoService.findPaged(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(processosPage);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ProcessoResponseDTO>> findByStatus(
            @PathVariable StatusProcessoEnum status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProcessoResponseDTO> processosPage = processoService.findByStatus(status, page, size);
        return ResponseEntity.ok(processosPage);
    }

    @GetMapping("/data-abertura")
    public ResponseEntity<Page<ProcessoResponseDTO>> findByDataAbertura(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProcessoResponseDTO> processosPage = processoService.findByDataAbertura(dataInicial, dataFinal, page, size);
        return ResponseEntity.ok(processosPage);
    }

    @GetMapping("/pessoa/id/{pessoaId}")
    public ResponseEntity<Page<ProcessoResponseDTO>> findByPessoaId(
            @PathVariable UUID pessoaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProcessoResponseDTO> processosPage = processoService.findByPessoaId(pessoaId, page, size);
        return ResponseEntity.ok(processosPage);
    }

    @GetMapping("/pessoa/cpf-cnpj/{cpfCnpj}")
    public ResponseEntity<Page<ProcessoResponseDTO>> findByPessoaCpfCnpj(
            @PathVariable String cpfCnpj,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProcessoResponseDTO> processosPage = processoService.findByPessoaCpfCnpj(cpfCnpj, page, size);
        return ResponseEntity.ok(processosPage);
    }

    // Business operations endpoints
    @PutMapping("/{id}/ativar")
    public ResponseEntity<ProcessoResponseDTO> ativar(@PathVariable UUID id) {
        ProcessoResponseDTO responseDTO = processoService.ativar(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}/suspender")
    public ResponseEntity<ProcessoResponseDTO> suspender(@PathVariable UUID id) {
        ProcessoResponseDTO responseDTO = processoService.suspender(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}/arquivar")
    public ResponseEntity<ProcessoResponseDTO> arquivar(@PathVariable UUID id) {
        ProcessoResponseDTO responseDTO = processoService.arquivar(id);
        return ResponseEntity.ok(responseDTO);
    }

    // Parte Envolvida endpoints
    @PostMapping("/{id}/partes-envolvidas")
    public ResponseEntity<ProcessoResponseDTO> addParteEnvolvida(
            @PathVariable UUID id,
            @Valid @RequestBody ParteEnvolvidaRequestDTO requestDTO) {
        ProcessoResponseDTO responseDTO = processoService.addParteEnvolvida(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/{id}/partes-envolvidas/batch")
    public ResponseEntity<ProcessoResponseDTO> addPartesEnvolvidas(
            @PathVariable UUID id,
            @Valid @RequestBody List<ParteEnvolvidaRequestDTO> requestDTOs) {
        ProcessoResponseDTO responseDTO = processoService.addPartesEnvolvidas(id, requestDTOs);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}/partes-envolvidas/{parteId}")
    public ResponseEntity<ProcessoResponseDTO> removeParteEnvolvida(
            @PathVariable UUID id,
            @PathVariable UUID parteId) {
        ProcessoResponseDTO responseDTO = processoService.removeParteEnvolvida(id, parteId);
        return ResponseEntity.ok(responseDTO);
    }

    // Ação endpoints
    @PostMapping("/{id}/acoes")
    public ResponseEntity<ProcessoResponseDTO> addAcaoProcesso(
            @PathVariable UUID id,
            @Valid @RequestBody AcaoRequestDTO requestDTO) {
        ProcessoResponseDTO responseDTO = processoService.addAcaoProcesso(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/{id}/acoes/batch")
    public ResponseEntity<ProcessoResponseDTO> addAcoesProcesso(
            @PathVariable UUID id,
            @Valid @RequestBody List<AcaoRequestDTO> requestDTOs) {
        ProcessoResponseDTO responseDTO = processoService.addAcoesProcesso(id, requestDTOs);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}/acoes/{acaoId}")
    public ResponseEntity<ProcessoResponseDTO> removeAcaoProcesso(
            @PathVariable UUID id,
            @PathVariable UUID acaoId) {
        ProcessoResponseDTO responseDTO = processoService.removeAcaoProcesso(id, acaoId);
        return ResponseEntity.ok(responseDTO);
    }
}