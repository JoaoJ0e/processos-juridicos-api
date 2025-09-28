package com.attus.sgpj.modules.processo.domain;

import com.attus.sgpj.modules.acao.domain.Acao;
import com.attus.sgpj.modules.acao.domain.TipoAcaoEnum;
import com.attus.sgpj.modules.parteenvolvida.domain.ParteEnvolvida;
import com.attus.sgpj.modules.parteenvolvida.domain.TipoParteEnvolvidaEnum;
import com.attus.sgpj.modules.pessoa.domain.Pessoa;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessoDomainTest {

    @Test
    void deveCriarProcessoComStatusAtivoEDataAtual() {
        String numero = "12345678901234567890";
        String descricao = "Processo de Teste";

        Processo processo = Processo.create(numero, descricao);

        assertThat(processo.getNumero()).isEqualTo(numero);
        assertThat(processo.getDescricao()).isEqualTo(descricao);
        assertThat(processo.getStatusProcesso()).isEqualTo(StatusProcessoEnum.ATIVO);
        assertThat(processo.getDataAbertura()).isEqualTo(LocalDate.now());
        assertThat(processo.getParteEnvolvidas()).isEmpty();
        assertThat(processo.getAcoes()).isEmpty();
    }

    @Test
    void deveCriarProcessoComDataEspecifica() {
        String numero = "12345678901234567890";
        String descricao = "Processo de Teste";
        LocalDate dataEspecifica = LocalDate.of(2024, 1, 15);

        Processo processo = Processo.create(numero, descricao, dataEspecifica);

        assertThat(processo.getNumero()).isEqualTo(numero);
        assertThat(processo.getDescricao()).isEqualTo(descricao);
        assertThat(processo.getDataAbertura()).isEqualTo(dataEspecifica);
        assertThat(processo.getStatusProcesso()).isEqualTo(StatusProcessoEnum.ATIVO);
    }

    @Test
    void deveAtualizarDadosDoProcesso() {
        Processo processo = Processo.create("11111111111111111111", "Descrição Original", LocalDate.now().minusDays(1));

        String novoNumero = "22222222222222222222";
        String novaDescricao = "Descrição Atualizada";
        LocalDate novaData = LocalDate.now();

        processo.update(novoNumero, novaDescricao, novaData);

        assertThat(processo.getNumero()).isEqualTo(novoNumero);
        assertThat(processo.getDescricao()).isEqualTo(novaDescricao);
        assertThat(processo.getDataAbertura()).isEqualTo(novaData);
    }

    @Test
    void naoDeveAtualizarComDadosNulosOuVazios() {
        String numeroOriginal = "11111111111111111111";
        String descricaoOriginal = "Descrição Original";
        LocalDate dataOriginal = LocalDate.now().minusDays(1);

        Processo processo = Processo.create(numeroOriginal, descricaoOriginal, dataOriginal);

        processo.update(null, "", null);

        assertThat(processo.getNumero()).isEqualTo(numeroOriginal);
        assertThat(processo.getDescricao()).isEqualTo(descricaoOriginal);
        assertThat(processo.getDataAbertura()).isEqualTo(dataOriginal);
    }

    @Test
    void deveAdicionarParteEnvolvida() {
        Processo processo = Processo.create("12345678901234567890", "Processo Teste");
        Pessoa pessoa = Pessoa.create("João Silva", "12345678901", "joao.silva@email.com", "11999999999");
        ParteEnvolvida parte = ParteEnvolvida.create(pessoa, processo, TipoParteEnvolvidaEnum.AUTOR);

        processo.addParte(parte);

        assertThat(processo.getParteEnvolvidas()).hasSize(1);
        assertThat(processo.getParteEnvolvidas().get(0)).isEqualTo(parte);
        assertThat(parte.getProcesso()).isEqualTo(processo);
    }

    @Test
    void deveRemoverParteEnvolvida() {
        Processo processo = Processo.create("12345678901234567890", "Processo Teste");
        Pessoa pessoa = Pessoa.create("João Silva", "12345678901", "joao.silva@email.com", "11999999999");
        ParteEnvolvida parte = ParteEnvolvida.create(pessoa, processo, TipoParteEnvolvidaEnum.AUTOR);

        processo.addParte(parte);
        assertThat(processo.getParteEnvolvidas()).hasSize(1);

        processo.removeParte(parte);

        assertThat(processo.getParteEnvolvidas()).isEmpty();
    }

    @Test
    void deveRemoverParteEnvolvidaPorId() {
        Processo processo = Processo.create("12345678901234567890", "Processo Teste");
        Pessoa pessoa = Pessoa.create("João Silva", "12345678901", "joao.silva@email.com", "11999999999");
        ParteEnvolvida parte = ParteEnvolvida.create(pessoa, processo, TipoParteEnvolvidaEnum.AUTOR);
        UUID parteId = UUID.randomUUID();
        parte.setId(parteId);

        processo.addParte(parte);
        assertThat(processo.getParteEnvolvidas()).hasSize(1);

        processo.removeParteById(parteId);

        assertThat(processo.getParteEnvolvidas()).isEmpty();
    }

    @Test
    void deveAdicionarAcao() {
        Processo processo = Processo.create("12345678901234567890", "Processo Teste");
        Acao acao = Acao.create(TipoAcaoEnum.PETICAO, "Petição Inicial", processo);

        processo.adicionarAcao(acao);

        assertThat(processo.getAcoes()).hasSize(1);
        assertThat(processo.getAcoes().get(0)).isEqualTo(acao);
        assertThat(acao.getProcesso()).isEqualTo(processo);
    }

    @Test
    void deveRemoverAcao() {
        Processo processo = Processo.create("12345678901234567890", "Processo Teste");
        Acao acao = Acao.create(TipoAcaoEnum.PETICAO, "Petição Inicial", processo);

        processo.adicionarAcao(acao);
        assertThat(processo.getAcoes()).hasSize(1);

        processo.removeAcao(acao);

        assertThat(processo.getAcoes()).isEmpty();
    }

    @Test
    void deveRemoverAcaoPorId() {
        Processo processo = Processo.create("12345678901234567890", "Processo Teste");
        Acao acao = Acao.create(TipoAcaoEnum.PETICAO, "Petição Inicial", processo);
        UUID acaoId = UUID.randomUUID();
        acao.setId(acaoId);

        processo.adicionarAcao(acao);
        assertThat(processo.getAcoes()).hasSize(1);

        processo.removeAcaoById(acaoId);

        assertThat(processo.getAcoes()).isEmpty();
    }

    @Test
    void devePermitirArquivamentoComPartesEAcoesObrigatorias() {
        Processo processo = criarProcessoComPartesEAcoesObrigatorias();

        boolean podeArquivar = processo.podeArquivar();

        assertThat(podeArquivar).isTrue();
    }

    @Test
    void naoDevePermitirArquivamentoSemPartesObrigatorias() {
        Processo processo = Processo.create("12345678901234567890", "Processo Incompleto");

        // Adiciona apenas ações obrigatórias, mas não as partes
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.PETICAO, "Petição Inicial", processo));
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.AUDIENCIA, "Audiência de Instrução", processo));
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.SENTENCA, "Sentença Final", processo));

        boolean podeArquivar = processo.podeArquivar();

        assertThat(podeArquivar).isFalse();
    }

    @Test
    void naoDevePermitirArquivamentoSemAcoesObrigatorias() {
        Processo processo = Processo.create("12345678901234567890", "Processo Incompleto");

        // Adiciona apenas partes obrigatórias, mas não as ações
        Pessoa autor = Pessoa.create("João Silva", "12345678901", "joao.silva@email.com", "11999999999");
        Pessoa reu = Pessoa.create("Maria Santos", "98765432100", "maria.santos@email.com", "11888888888");
        Pessoa advogado = Pessoa.create("Carlos Advogado", "11122233344", "carlos.advogado@email.com", "11777777777");

        processo.addParte(ParteEnvolvida.create(autor, processo, TipoParteEnvolvidaEnum.AUTOR));
        processo.addParte(ParteEnvolvida.create(reu, processo, TipoParteEnvolvidaEnum.REU));
        processo.addParte(ParteEnvolvida.create(advogado, processo, TipoParteEnvolvidaEnum.ADVOGADO));

        boolean podeArquivar = processo.podeArquivar();

        assertThat(podeArquivar).isFalse();
    }

    @Test
    void naoDevePermitirArquivamentoSemParteAutor() {
        Processo processo = Processo.create("12345678901234567890", "Processo Incompleto");

        // Adiciona REU e ADVOGADO, mas não AUTOR
        Pessoa reu = Pessoa.create("Maria Santos", "98765432100", "maria.santos@email.com", "11888888888");
        Pessoa advogado = Pessoa.create("Carlos Advogado", "11122233344", "carlos.advogado@email.com", "11777777777");

        processo.addParte(ParteEnvolvida.create(reu, processo, TipoParteEnvolvidaEnum.REU));
        processo.addParte(ParteEnvolvida.create(advogado, processo, TipoParteEnvolvidaEnum.ADVOGADO));

        // Adiciona ações obrigatórias
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.PETICAO, "Petição Inicial", processo));
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.AUDIENCIA, "Audiência de Instrução", processo));
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.SENTENCA, "Sentença Final", processo));

        boolean podeArquivar = processo.podeArquivar();

        assertThat(podeArquivar).isFalse();
    }

    @Test
    void naoDevePermitirArquivamentoSemAcaoPeticao() {
        Processo processo = Processo.create("12345678901234567890", "Processo Incompleto");

        // Adiciona partes obrigatórias
        Pessoa autor = Pessoa.create("João Silva", "12345678901", "joao.silva@email.com", "11999999999");
        Pessoa reu = Pessoa.create("Maria Santos", "98765432100", "maria.santos@email.com", "11888888888");
        Pessoa advogado = Pessoa.create("Carlos Advogado", "11122233344", "carlos.advogado@email.com", "11777777777");

        processo.addParte(ParteEnvolvida.create(autor, processo, TipoParteEnvolvidaEnum.AUTOR));
        processo.addParte(ParteEnvolvida.create(reu, processo, TipoParteEnvolvidaEnum.REU));
        processo.addParte(ParteEnvolvida.create(advogado, processo, TipoParteEnvolvidaEnum.ADVOGADO));

        // Adiciona AUDIENCIA e SENTENCA, mas não PETICAO
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.AUDIENCIA, "Audiência de Instrução", processo));
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.SENTENCA, "Sentença Final", processo));

        boolean podeArquivar = processo.podeArquivar();

        assertThat(podeArquivar).isFalse();
    }

    @Test
    void naoDevePermitirArquivamentoSemAcaoAudiencia() {
        Processo processo = Processo.create("12345678901234567890", "Processo Incompleto");

        // Adiciona partes obrigatórias
        Pessoa autor = Pessoa.create("João Silva", "12345678901", "joao.silva@email.com", "11999999999");
        Pessoa reu = Pessoa.create("Maria Santos", "98765432100", "maria.santos@email.com", "11888888888");
        Pessoa advogado = Pessoa.create("Carlos Advogado", "11122233344", "carlos.advogado@email.com", "11777777777");

        processo.addParte(ParteEnvolvida.create(autor, processo, TipoParteEnvolvidaEnum.AUTOR));
        processo.addParte(ParteEnvolvida.create(reu, processo, TipoParteEnvolvidaEnum.REU));
        processo.addParte(ParteEnvolvida.create(advogado, processo, TipoParteEnvolvidaEnum.ADVOGADO));

        // Adiciona PETICAO e SENTENCA, mas não AUDIENCIA
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.PETICAO, "Petição Inicial", processo));
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.SENTENCA, "Sentença Final", processo));

        boolean podeArquivar = processo.podeArquivar();

        assertThat(podeArquivar).isFalse();
    }

    @Test
    void naoDevePermitirArquivamentoSemSentencaOuDesistencia() {
        Processo processo = Processo.create("12345678901234567890", "Processo Incompleto");

        // Adiciona partes obrigatórias
        Pessoa autor = Pessoa.create("João Silva", "12345678901", "joao.silva@email.com", "11999999999");
        Pessoa reu = Pessoa.create("Maria Santos", "98765432100", "maria.santos@email.com", "11888888888");
        Pessoa advogado = Pessoa.create("Carlos Advogado", "11122233344", "carlos.advogado@email.com", "11777777777");

        processo.addParte(ParteEnvolvida.create(autor, processo, TipoParteEnvolvidaEnum.AUTOR));
        processo.addParte(ParteEnvolvida.create(reu, processo, TipoParteEnvolvidaEnum.REU));
        processo.addParte(ParteEnvolvida.create(advogado, processo, TipoParteEnvolvidaEnum.ADVOGADO));

        // Adiciona PETICAO e AUDIENCIA, mas não SENTENCA nem DESISTENCIA
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.PETICAO, "Petição Inicial", processo));
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.AUDIENCIA, "Audiência de Instrução", processo));

        boolean podeArquivar = processo.podeArquivar();

        assertThat(podeArquivar).isFalse();
    }

    @Test
    void devePermitirArquivamentoComDesistenciaAoInvesDeSentenca() {
        Processo processo = Processo.create("12345678901234567890", "Processo com Desistência");

        // Adiciona partes obrigatórias
        Pessoa autor = Pessoa.create("João Silva", "12345678901", "joao.silva@email.com", "11999999999");
        Pessoa reu = Pessoa.create("Maria Santos", "98765432100", "maria.santos@email.com", "11888888888");
        Pessoa advogado = Pessoa.create("Carlos Advogado", "11122233344", "carlos.advogado@email.com", "11777777777");

        processo.addParte(ParteEnvolvida.create(autor, processo, TipoParteEnvolvidaEnum.AUTOR));
        processo.addParte(ParteEnvolvida.create(reu, processo, TipoParteEnvolvidaEnum.REU));
        processo.addParte(ParteEnvolvida.create(advogado, processo, TipoParteEnvolvidaEnum.ADVOGADO));

        // Adiciona ações obrigatórias com DESISTENCIA ao invés de SENTENCA
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.PETICAO, "Petição Inicial", processo));
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.AUDIENCIA, "Audiência de Instrução", processo));
        processo.adicionarAcao(Acao.create(TipoAcaoEnum.DESISTENCIA, "Desistência do Processo", processo));

        boolean podeArquivar = processo.podeArquivar();

        assertThat(podeArquivar).isTrue();
    }

    @Test
    void deveInicializarStateAposCarregar() {
        Processo processo = Processo.create("12345678901234567890", "Processo Teste");

        // Simula o comportamento do @PostLoad/@PostConstruct
        processo.atualizarState();

        assertThat(processo.getState()).isNotNull();
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