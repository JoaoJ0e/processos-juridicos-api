package com.attus.sgpj.modules.parteenvolvida.domain.dto;

import com.attus.sgpj.modules.parteenvolvida.domain.TipoParteEnvolvidaEnum;

import java.util.UUID;

public record ParteEnvolvidaRequestDTO(
        UUID pessoaId,
        TipoParteEnvolvidaEnum tipo
) {}
