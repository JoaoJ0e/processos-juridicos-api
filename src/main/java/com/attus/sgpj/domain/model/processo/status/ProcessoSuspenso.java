package com.attus.sgpj.domain.model.processo.status;

import com.attus.sgpj.domain.model.processo.Processo;
import com.attus.sgpj.domain.model.processo.StatusProcessoEnum;

public class ProcessoSuspenso implements ProcessoState {

    @Override
    public void ativar(Processo processo) {
        processo.setStatusProcesso(StatusProcessoEnum.ATIVO);
    }

    @Override
    public void suspender(Processo processo) {
        throw new IllegalArgumentException("Não é possível ativar um processo já ativo.");
    }

    @Override
    public void arquivar(Processo processo) {
        processo.setStatusProcesso(StatusProcessoEnum.ARQUIVADO);
    }
}
