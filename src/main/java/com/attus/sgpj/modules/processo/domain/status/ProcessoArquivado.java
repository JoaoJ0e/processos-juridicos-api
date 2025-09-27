package com.attus.sgpj.modules.processo.domain.status;

import com.attus.sgpj.modules.processo.domain.Processo;
import com.attus.sgpj.modules.processo.exception.ProcessoInvalidStateTransitionException;

public class ProcessoArquivado implements ProcessoState {

    @Override
    public void ativar(Processo processo) {
        throw new ProcessoInvalidStateTransitionException("Não é possível ativar um processo arquivado.");
    }

    @Override
    public void suspender(Processo processo) {
        throw new ProcessoInvalidStateTransitionException("Não é possível suspender um processo arquivado.");
    }

    @Override
    public void arquivar(Processo processo) {
        throw new ProcessoInvalidStateTransitionException("Não é possível arquivar um processo já arquivado.");

    }
}
