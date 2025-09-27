package com.attus.sgpj.modules.acao.domain.dto;

import com.attus.sgpj.modules.acao.domain.Acao;
import com.attus.sgpj.modules.acao.domain.TipoAcaoEnum;

import java.time.LocalDate;
import java.util.UUID;

public record AcaoResponseDTO(
        UUID id,
        UUID processoId,
        TipoAcaoEnum tipo,
        String descricao,
        LocalDate dataRegistro
) {
    public static AcaoResponseDTO fromDomain(Acao acao) {
        return new AcaoResponseDTO(
                acao.getId(),
                acao.getProcesso() != null ? acao.getProcesso().getId() : null,
                acao.getTipo(),
                acao.getDescricao(),
                acao.getDataRegistro()
        );
    }
}
