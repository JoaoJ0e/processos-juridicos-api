package com.attus.sgpj.modules.processo.domain.status;

import com.attus.sgpj.modules.processo.domain.Processo;
import com.attus.sgpj.modules.processo.domain.StatusProcessoEnum;
import com.attus.sgpj.modules.processo.exception.ProcessoInvalidStateTransitionException;

public class ProcessoSuspenso implements ProcessoState {

    @Override
    public void ativar(Processo processo) {
        processo.setStatusProcesso(StatusProcessoEnum.ATIVO);
    }

    @Override
    public void suspender(Processo processo) {
        throw new ProcessoInvalidStateTransitionException("Não é possível ativar um processo já ativo.");
    }

    @Override
    public void arquivar(Processo processo) {
        processo.setStatusProcesso(StatusProcessoEnum.ARQUIVADO);
    }
}
