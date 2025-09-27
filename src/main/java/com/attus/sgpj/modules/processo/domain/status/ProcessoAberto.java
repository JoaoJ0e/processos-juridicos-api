package com.attus.sgpj.modules.processo.domain.status;

import com.attus.sgpj.modules.processo.domain.Processo;
import com.attus.sgpj.modules.processo.domain.StatusProcessoEnum;
import com.attus.sgpj.modules.processo.exception.ProcessoInvalidStateTransitionException;

public class ProcessoAberto implements ProcessoState {

    @Override
    public void ativar(Processo processo) {
        throw new ProcessoInvalidStateTransitionException("Não é possível ativar um processo já ativo.");
    }

    @Override
    public void suspender(Processo processo) {
        processo.setStatusProcesso(StatusProcessoEnum.SUSPENSO);
    }

    @Override
    public void arquivar(Processo processo) {
        if (!processo.podeArquivar()) {
            throw new ProcessoInvalidStateTransitionException("Não é possível arquivar sem partes e ações obrigatórias.");
        }
        processo.setStatusProcesso(StatusProcessoEnum.ARQUIVADO);
    }


}
