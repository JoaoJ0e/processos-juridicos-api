package com.attus.sgpj.domain.model.processo.status;

import com.attus.sgpj.domain.model.processo.Processo;

public class ProcessoArquivado implements ProcessoState {

    @Override
    public void ativar(Processo processo) {
        throw new IllegalArgumentException("Não é possível ativar um processo arquivado.");
    }

    @Override
    public void suspender(Processo processo) {
        throw new IllegalStateException("Não é possível suspender um processo arquivado.");
    }

    @Override
    public void arquivar(Processo processo) {
        throw new IllegalStateException("Não é possível arquivar um processo já arquivado.");

    }
}
