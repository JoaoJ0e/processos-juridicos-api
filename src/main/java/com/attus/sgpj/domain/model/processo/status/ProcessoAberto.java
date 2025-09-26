package com.attus.sgpj.domain.model.processo.status;

import com.attus.sgpj.domain.model.processo.Processo;
import com.attus.sgpj.domain.model.processo.StatusProcessoEnum;

public class ProcessoAberto implements ProcessoState {

    @Override
    public void ativar(Processo processo) {
        throw new IllegalArgumentException("Não é possível ativar um processo já ativo.");
    }

    @Override
    public void suspender(Processo processo) {
        processo.setStatusProcesso(StatusProcessoEnum.SUSPENSO);
    }

    @Override
    public void arquivar(Processo processo) {
        if (!processo.podeArquivar()) {
            throw new IllegalStateException("Não é possível arquivar sem partes e ações obrigatórias."); //;TODO: Montar exceptions personalizadas
        }
        processo.setStatusProcesso(StatusProcessoEnum.ARQUIVADO);
    }


}
