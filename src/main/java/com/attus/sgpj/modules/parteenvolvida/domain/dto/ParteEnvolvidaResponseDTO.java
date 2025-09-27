package com.attus.sgpj.modules.parteenvolvida.domain.dto;

import com.attus.sgpj.modules.parteenvolvida.domain.ParteEnvolvida;
import com.attus.sgpj.modules.parteenvolvida.domain.TipoParteEnvolvidaEnum;
import com.attus.sgpj.modules.pessoa.domain.dto.PessoaResponseDTO;

import java.util.UUID;

public record ParteEnvolvidaResponseDTO(
        UUID id,
        UUID processoId,
        PessoaResponseDTO pessoa,
        TipoParteEnvolvidaEnum tipo
) {
    public static ParteEnvolvidaResponseDTO fromDomain(ParteEnvolvida parte) {
        return new ParteEnvolvidaResponseDTO(
                parte.getId(),
                parte.getProcesso() != null ? parte.getProcesso().getId() : null,
                parte.getPessoa() != null ? PessoaResponseDTO.fromDomain(parte.getPessoa()) : null,
                parte.getTipo()
        );
    }
}
