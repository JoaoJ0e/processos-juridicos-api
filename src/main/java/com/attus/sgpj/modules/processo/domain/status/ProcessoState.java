package com.attus.sgpj.modules.processo.domain.status;

import com.attus.sgpj.modules.processo.domain.Processo;

public interface ProcessoState {
    void ativar(Processo processo);

    void suspender(Processo processo);

    void arquivar(Processo processo);
}
