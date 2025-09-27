package com.attus.sgpj.modules.processo.domain.dto;

import com.attus.sgpj.modules.parteenvolvida.domain.dto.ParteEnvolvidaRequestDTO;

import java.time.LocalDate;
import java.util.List;

public record ProcessoRequestDTO(
        String numero,
        String descricao,
        List<ParteEnvolvidaRequestDTO> partesEnvolvidas, //TODO: Remover por enquanto ou fazer não precisar de id processo
        LocalDate dataAbertura
) {
    public ProcessoRequestDTO(String numero, String descricao, List<ParteEnvolvidaRequestDTO> partesEnvolvidas) {
        this(numero, descricao, partesEnvolvidas, LocalDate.now());
    }
}
