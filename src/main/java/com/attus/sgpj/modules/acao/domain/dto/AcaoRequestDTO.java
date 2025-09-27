package com.attus.sgpj.modules.acao.domain.dto;

import com.attus.sgpj.modules.acao.domain.TipoAcaoEnum;

public record AcaoRequestDTO(
        TipoAcaoEnum tipo,
        String descricao
) {
}
