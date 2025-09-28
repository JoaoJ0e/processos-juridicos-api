package com.attus.sgpj.modules.processo.application;

import com.attus.sgpj.modules.acao.domain.dto.AcaoRequestDTO;
import com.attus.sgpj.modules.parteenvolvida.domain.dto.ParteEnvolvidaRequestDTO;
import com.attus.sgpj.modules.processo.domain.StatusProcessoEnum;
import com.attus.sgpj.modules.processo.domain.dto.ProcessoRequestDTO;
import com.attus.sgpj.modules.processo.domain.dto.ProcessoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Processos", description = "Gerenciamento de processos jurídicos")
public class ProcessoController {

    private final ProcessoService processoService;

    @PostMapping
    @Operation(summary = "Criar processo", description = "Cria um novo processo a partir dos dados informados")
    public ResponseEntity<ProcessoResponseDTO> create(@Valid @RequestBody ProcessoRequestDTO requestDTO) {
        ProcessoResponseDTO responseDTO = processoService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar processo", description = "Atualiza os dados de um processo existente")
    public ResponseEntity<ProcessoResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProcessoRequestDTO requestDTO) {
        ProcessoResponseDTO responseDTO = processoService.update(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Retorna os dados de um processo pelo ID")
    public ResponseEntity<ProcessoResponseDTO> findById(@PathVariable UUID id) {
        ProcessoResponseDTO responseDTO = processoService.findById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    @Operation(summary = "Listar processos paginados", description = "Lista todos os processos com paginação e ordenação")
    public ResponseEntity<Page<ProcessoResponseDTO>> findPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        Page<ProcessoResponseDTO> processosPage = processoService.findPaged(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(processosPage);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Buscar por status", description = "Lista processos filtrados pelo status")
    public ResponseEntity<Page<ProcessoResponseDTO>> findByStatus(
            @PathVariable StatusProcessoEnum status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProcessoResponseDTO> processosPage = processoService.findByStatus(status, page, size);
        return ResponseEntity.ok(processosPage);
    }

    @GetMapping("/data-abertura")
    @Operation(summary = "Buscar por data de abertura", description = "Lista processos entre duas datas de abertura")
    public ResponseEntity<Page<ProcessoResponseDTO>> findByDataAbertura(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProcessoResponseDTO> processosPage = processoService.findByDataAbertura(dataInicial, dataFinal, page, size);
        return ResponseEntity.ok(processosPage);
    }

    @GetMapping("/pessoa/id/{pessoaId}")
    @Operation(summary = "Buscar por pessoa (ID)", description = "Lista processos relacionados a uma pessoa via ID")
    public ResponseEntity<Page<ProcessoResponseDTO>> findByPessoaId(
            @PathVariable UUID pessoaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProcessoResponseDTO> processosPage = processoService.findByPessoaId(pessoaId, page, size);
        return ResponseEntity.ok(processosPage);
    }

    @GetMapping("/pessoa/cpf-cnpj/{cpfCnpj}")
    @Operation(summary = "Buscar por parte envolvida (CPF/CNPJ)", description = "Lista processos relacionados a uma parte envolvida via CPF/CNPJ")
    public ResponseEntity<Page<ProcessoResponseDTO>> findByCpfCnpjParteEnvolvida(
            @PathVariable String cpfCnpj,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProcessoResponseDTO> processosPage = processoService.findByCpfCnpjParteEnvolvida(cpfCnpj, page, size);
        return ResponseEntity.ok(processosPage);
    }

    @PutMapping("/{id}/ativar")
    @Operation(summary = "Ativar processo", description = "Ativa um processo inativo")
    public ResponseEntity<ProcessoResponseDTO> ativar(@PathVariable UUID id) {
        ProcessoResponseDTO responseDTO = processoService.ativar(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}/suspender")
    @Operation(summary = "Suspender processo", description = "Suspende temporariamente um processo")
    public ResponseEntity<ProcessoResponseDTO> suspender(@PathVariable UUID id) {
        ProcessoResponseDTO responseDTO = processoService.suspender(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}/arquivar")
    @Operation(summary = "Arquivar processo", description = "Arquiva permanentemente um processo")
    public ResponseEntity<ProcessoResponseDTO> arquivar(@PathVariable UUID id) {
        ProcessoResponseDTO responseDTO = processoService.arquivar(id);
        return ResponseEntity.ok(responseDTO);
    }

    // Parte Envolvida
    @PostMapping("/{id}/partes-envolvidas")
    @Operation(summary = "Adicionar parte envolvida", description = "Adiciona uma parte envolvida ao processo")
    public ResponseEntity<ProcessoResponseDTO> addParteEnvolvida(
            @PathVariable UUID id,
            @Valid @RequestBody ParteEnvolvidaRequestDTO requestDTO) {
        ProcessoResponseDTO responseDTO = processoService.addParteEnvolvida(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/{id}/partes-envolvidas/batch")
    @Operation(summary = "Adicionar partes envolvidas em lote", description = "Adiciona múltiplas partes envolvidas de uma vez")
    public ResponseEntity<ProcessoResponseDTO> addPartesEnvolvidas(
            @PathVariable UUID id,
            @Valid @RequestBody List<ParteEnvolvidaRequestDTO> requestDTOs) {
        ProcessoResponseDTO responseDTO = processoService.addPartesEnvolvidas(id, requestDTOs);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}/partes-envolvidas/{parteId}")
    @Operation(summary = "Remover parte envolvida", description = "Remove uma parte envolvida do processo")
    public ResponseEntity<ProcessoResponseDTO> removeParteEnvolvida(
            @PathVariable UUID id,
            @PathVariable UUID parteId) {
        ProcessoResponseDTO responseDTO = processoService.removeParteEnvolvida(id, parteId);
        return ResponseEntity.ok(responseDTO);
    }

    // Ações
    @PostMapping("/{id}/acoes")
    @Operation(summary = "Adicionar ação", description = "Adiciona uma ação ao processo")
    public ResponseEntity<ProcessoResponseDTO> addAcaoProcesso(
            @PathVariable UUID id,
            @Valid @RequestBody AcaoRequestDTO requestDTO) {
        ProcessoResponseDTO responseDTO = processoService.addAcaoProcesso(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/{id}/acoes/batch")
    @Operation(summary = "Adicionar ações em lote", description = "Adiciona múltiplas ações ao processo de uma vez")
    public ResponseEntity<ProcessoResponseDTO> addAcoesProcesso(
            @PathVariable UUID id,
            @Valid @RequestBody List<AcaoRequestDTO> requestDTOs) {
        ProcessoResponseDTO responseDTO = processoService.addAcoesProcesso(id, requestDTOs);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}/acoes/{acaoId}")
    @Operation(summary = "Remover ação", description = "Remove uma ação do processo")
    public ResponseEntity<ProcessoResponseDTO> removeAcaoProcesso(
            @PathVariable UUID id,
            @PathVariable UUID acaoId) {
        ProcessoResponseDTO responseDTO = processoService.removeAcaoProcesso(id, acaoId);
        return ResponseEntity.ok(responseDTO);
    }
}
