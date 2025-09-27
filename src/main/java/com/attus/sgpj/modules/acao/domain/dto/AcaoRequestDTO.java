package com.attus.sgpj.modules.acao.domain.dto;

import com.attus.sgpj.modules.acao.domain.TipoAcaoEnum;
import com.attus.sgpj.modules.processo.domain.Processo;

import java.util.UUID;

public record AcaoRequestDTO(
        UUID processoId,
        TipoAcaoEnum tipo,
        String descricao
) {
}
