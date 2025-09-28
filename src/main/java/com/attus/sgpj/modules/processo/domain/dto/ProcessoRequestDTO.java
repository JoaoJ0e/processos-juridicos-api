package com.attus.sgpj.modules.processo.domain.dto;

import java.time.LocalDate;

public record ProcessoRequestDTO(
        String numero,
        String descricao,
        LocalDate dataAbertura
) {
    public ProcessoRequestDTO(String numero, String descricao) {
        this(numero, descricao, LocalDate.now());
    }
}
