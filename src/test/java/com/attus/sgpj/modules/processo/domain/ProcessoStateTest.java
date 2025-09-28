package com.attus.sgpj.modules.processo.domain;

import com.attus.sgpj.modules.acao.domain.Acao;
import com.attus.sgpj.modules.acao.domain.TipoAcaoEnum;
import com.attus.sgpj.modules.parteenvolvida.domain.ParteEnvolvida;
import com.attus.sgpj.modules.parteenvolvida.domain.TipoParteEnvolvidaEnum;
import com.attus.sgpj.modules.pessoa.domain.Pessoa;
import com.attus.sgpj.modules.processo.domain.status.ProcessoAberto;
import com.attus.sgpj.modules.processo.domain.status.ProcessoArquivado;
import com.attus.sgpj.modules.processo.domain.status.ProcessoSuspenso;
import com.attus.sgpj.modules.processo.exception.ProcessoInvalidStateTransitionException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProcessoStateTest {

    @Test
    void deveIniciarProcessoComStatusAberto() {
        Processo processo = Processo.create("12345678901234567890", "Processo Teste");

        assertThat(processo.getStatusProcesso()).isEqualTo(StatusProcessoEnum.ATIVO);
        assertThat(processo.getState()).isInstanceOf(ProcessoAberto.class);
    }

    @Test
    void deveSuspenderProcessoAtivo() {
        Processo processo = Processo.create("12345678901234567890", "Processo Teste");

        processo.suspender();

        assertThat(processo.getStatusProcesso()).isEqualTo(StatusProcessoEnum.SUSPENSO);
        assertThat(processo.getState()).isInstanceOf(ProcessoSuspenso.class);
    }

    @Test
    void deveArquivarProcessoAtivoComPartesEAcoesObrigatorias() {
        Processo processo = criarProcessoComPartesEAcoesObrigatorias();

        processo.arquivar();

        assertThat(processo.getStatusProcesso()).isEqualTo(StatusProcessoEnum.ARQUIVADO);
        assertThat(processo.getState()).isInstanceOf(ProcessoArquivado.class);
    }

    @Test
    void deveLancarExcecaoAoTentarAtivarProcessoJaAtivo() {
        Processo processo = Processo.create("12345678901234567890", "Processo Teste");

        assertThatThrownBy(processo::ativar)
                .isInstanceOf(ProcessoInvalidStateTransitionException.class)
                .hasMessage("Não é possível ativar um processo já ativo.");
    }

    @Test
    void deveLancarExcecaoAoTentarArquivarProcessoSemPartesObrigatorias() {
        Processo processo = Processo.create("12345678901234567890", "Processo Incompleto");

        assertThatThrownBy(processo::arquivar)
                .isInstanceOf(ProcessoInvalidStateTransitionException.class)
                .hasMessage("Processo não pode ser arquivado. Deve possuir todas as partes obrigatórias (AUTOR, RÉU, ADVOGADO) e ações obrigatórias (PETIÇÃO, AUDIÊNCIA e SENTENÇA ou DESISTÊNCIA).");
    }

    @Test
    void deveAtivarProcessoSuspenso() {
        Processo processo = Processo.create("12345678901234567890", "Processo Teste");
        processo.suspender();

        processo.ativar();

        assertThat(processo.getStatusProcesso()).isEqualTo(StatusProcessoEnum.ATIVO);
        assertThat(processo.getState()).isInstanceOf(ProcessoAberto.class);
    }

    @Test
    void deveArquivarProcessoSuspenso() {
        Processo processo = criarProcessoComPartesEAcoesObrigatorias();
        processo.suspender();

        processo.arquivar();

        assertThat(processo.getStatusProcesso()).isEqualTo(StatusProcessoEnum.ARQUIVADO);
        assertThat(processo.getState()).isInstanceOf(ProcessoArquivado.class);
    }

    @Test
    void deveLancarExcecaoAoTentarSuspenderProcessoJaSuspenso() {
        Processo processo = Processo.create("12345678901234567890", "Processo Teste");
        processo.suspender();

        assertThatThrownBy(processo::suspender)
                .isInstanceOf(ProcessoInvalidStateTransitionException.class)
                .hasMessage("Não é possível suspender um processo já suspenso.");
    }

    @Test
    void deveLancarExcecaoAoTentarAtivarProcessoArquivado() {
        Processo processo = criarProcessoComPartesEAcoesObrigatorias();
        processo.arquivar();

        assertThatThrownBy(processo::ativar)
                .isInstanceOf(ProcessoInvalidStateTransitionException.class)
                .hasMessage("Não é possível ativar um processo arquivado.");
    }

    @Test
    void deveLancarExcecaoAoTentarSuspenderProcessoArquivado() {
        Processo processo = criarProcessoComPartesEAcoesObrigatorias();
        processo.arquivar();

        assertThatThrownBy(processo::suspender)
                .isInstanceOf(ProcessoInvalidStateTransitionException.class)
                .hasMessage("Não é possível suspender um processo arquivado.");
    }

    @Test
    void deveLancarExcecaoAoTentarArquivarProcessoJaArquivado() {
        Processo processo = criarProcessoComPartesEAcoesObrigatorias();
        processo.arquivar();

        assertThatThrownBy(processo::arquivar)
                .isInstanceOf(ProcessoInvalidStateTransitionException.class)
                .hasMessage("Não é possível arquivar um processo já arquivado.");
    }

    @Test
    void deveAtualizarStateCorretamenteParaAtivo() {
        Processo processo = Processo.create("12345678901234567890", "Processo Teste");
        processo.setStatusProcesso(StatusProcessoEnum.ATIVO);

        processo.atualizarState();

        assertThat(processo.getState()).isInstanceOf(ProcessoAberto.class);
    }

    @Test
    void deveAtualizarStateCorretamenteParaSuspenso() {
        Processo processo = Processo.create("12345678901234567890", "Processo Teste");
        processo.setStatusProcesso(StatusProcessoEnum.SUSPENSO);

        processo.atualizarState();

        assertThat(processo.getState()).isInstanceOf(ProcessoSuspenso.class);
    }

    @Test
    void deveAtualizarStateCorretamenteParaArquivado() {
        Processo processo = Processo.create("12345678901234567890", "Processo Teste");
        processo.setStatusProcesso(StatusProcessoEnum.ARQUIVADO);

        processo.atualizarState();

        assertThat(processo.getState()).isInstanceOf(ProcessoArquivado.class);
    }

    @Test
    void devePermitirTransicaoCompleta() {
        Processo processo = criarProcessoComPartesEAcoesObrigatorias();

        // ATIVO -> SUSPENSO
        processo.suspender();
        assertThat(processo.getStatusProcesso()).isEqualTo(StatusProcessoEnum.SUSPENSO);
        assertThat(processo.getState()).isInstanceOf(ProcessoSuspenso.class);

        // SUSPENSO -> ATIVO
        processo.ativar();
        assertThat(processo.getStatusProcesso()).isEqualTo(StatusProcessoEnum.ATIVO);
        assertThat(processo.getState()).isInstanceOf(ProcessoAberto.class);

        // ATIVO -> ARQUIVADO
        processo.arquivar();
        assertThat(processo.getStatusProcesso()).isEqualTo(StatusProcessoEnum.ARQUIVADO);
        assertThat(processo.getState()).isInstanceOf(ProcessoArquivado.class);
    }

    @Test
    void devePermitirTransicaoSuspensoParaArquivado() {
        Processo processo = criarProcessoComPartesEAcoesObrigatorias();

        // ATIVO -> SUSPENSO
        processo.suspender();
        assertThat(processo.getStatusProcesso()).isEqualTo(StatusProcessoEnum.SUSPENSO);

        // SUSPENSO -> ARQUIVADO
        processo.arquivar();
        assertThat(processo.getStatusProcesso()).isEqualTo(StatusProcessoEnum.ARQUIVADO);
        assertThat(processo.getState()).isInstanceOf(ProcessoArquivado.class);
    }

    @Test
    void deveArquivarProcessoAtivoComDesistenciaAoInvesDeSentenca() {
        Processo processo = criarProcessoComPartesEAcoesObrigatorias();

        // Remove sentença e adiciona desistência
        processo.getAcoes().removeIf(a -> a.getTipo() == TipoAcaoEnum.SENTENCA);
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.DESISTENCIA, "Desistência do Processo", processo));

        processo.arquivar();

        assertThat(processo.getStatusProcesso()).isEqualTo(StatusProcessoEnum.ARQUIVADO);
        assertThat(processo.getState()).isInstanceOf(ProcessoArquivado.class);
    }

    private Processo criarProcessoComPartesEAcoesObrigatorias() {
        Processo processo = Processo.create("12345678901234567890", "Processo Completo");

        // Adiciona partes obrigatórias
        Pessoa autor = Pessoa.create("João Silva", "12345678901", "joao.silva@email.com", "11999999999");
        Pessoa reu = Pessoa.create("Maria Santos", "98765432100", "maria.santos@email.com", "11888888888");
        Pessoa advogado = Pessoa.create("Carlos Advogado", "11122233344", "carlos.advogado@email.com", "11777777777");

        processo.addParte(ParteEnvolvida.create(autor, processo, TipoParteEnvolvidaEnum.AUTOR));
        processo.addParte(ParteEnvolvida.create(reu, processo, TipoParteEnvolvidaEnum.REU));
        processo.addParte(ParteEnvolvida.create(advogado, processo, TipoParteEnvolvidaEnum.ADVOGADO));

        // Adiciona ações obrigatórias
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.PETICAO, "Petição Inicial", processo));
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.AUDIENCIA, "Audiência de Instrução", processo));
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.SENTENCA, "Sentença Final", processo));

        return processo;
    }
}