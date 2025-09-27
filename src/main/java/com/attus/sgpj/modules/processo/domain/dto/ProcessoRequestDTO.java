package com.attus.sgpj.modules.processo.domain.dto;

import com.attus.sgpj.modules.parteenvolvida.domain.dto.ParteEnvolvidaRequestDTO;

import java.time.LocalDate;
import java.util.List;

public record ProcessoRequestDTO(
        String numero,
        String descricao,
        LocalDate dataAbertura
) {
    public ProcessoRequestDTO(String numero, String descricao) {
        this(numero, descricao, LocalDate.now());
    }
}
