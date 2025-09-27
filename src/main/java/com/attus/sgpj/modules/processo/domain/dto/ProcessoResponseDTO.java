package com.attus.sgpj.modules.processo.domain.dto;

import com.attus.sgpj.modules.acao.domain.dto.AcaoResponseDTO;
import com.attus.sgpj.modules.parteenvolvida.domain.dto.ParteEnvolvidaResponseDTO;
import com.attus.sgpj.modules.processo.domain.Processo;
import com.attus.sgpj.modules.processo.domain.StatusProcessoEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProcessoResponseDTO(
        UUID id,
        String numero,
        String descricao,
        LocalDate dataAbertura,
        StatusProcessoEnum statusProcesso,
        List<ParteEnvolvidaResponseDTO> partesEnvolvidas,
        List<AcaoResponseDTO> acoes
) {
    public static ProcessoResponseDTO fromDomain(Processo processo) {
        return new ProcessoResponseDTO(
                processo.getId(),
                processo.getNumero(),
                processo.getDescricao(),
                processo.getDataAbertura(),
                processo.getStatusProcesso(),
                processo.getParteEnvolvidas().stream()
                        .map(ParteEnvolvidaResponseDTO::fromDomain)
                        .toList(),
                processo.getAcoes().stream()
                        .map(AcaoResponseDTO::fromDomain)
                        .toList()
        );
    }
}
