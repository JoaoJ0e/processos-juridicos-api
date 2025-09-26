package com.attus.sgpj.domain.model.processo.status;

import com.attus.sgpj.domain.model.processo.Processo;

public interface ProcessoState {
    void ativar(Processo processo);

    void suspender(Processo processo);

    void arquivar(Processo processo);
}
