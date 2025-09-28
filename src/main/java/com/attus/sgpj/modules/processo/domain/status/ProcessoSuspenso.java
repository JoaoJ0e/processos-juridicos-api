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
        throw new ProcessoInvalidStateTransitionException("Não é possível suspender um processo já suspenso.");
    }

    @Override
    public void arquivar(Processo processo) {
        if (!processo.podeArquivar()) {
            throw new ProcessoInvalidStateTransitionException("Processo não pode ser arquivado. Deve possuir todas as partes obrigatórias (AUTOR, RÉU, ADVOGADO) e ações obrigatórias (PETIÇÃO, AUDIÊNCIA e SENTENÇA ou DESISTÊNCIA).");
        }
        processo.setStatusProcesso(StatusProcessoEnum.ARQUIVADO);
    }
}