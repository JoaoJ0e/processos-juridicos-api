package com.attus.sgpj.modules.processo.domain;

import com.attus.sgpj.modules.acao.domain.Acao;
import com.attus.sgpj.modules.parteenvolvida.domain.ParteEnvolvida;
import com.attus.sgpj.modules.acao.domain.TipoAcaoEnum;
import com.attus.sgpj.modules.parteenvolvida.domain.TipoParteEnvolvidaEnum;
import com.attus.sgpj.modules.processo.domain.status.ProcessoAberto;
import com.attus.sgpj.modules.processo.domain.status.ProcessoArquivado;
import com.attus.sgpj.modules.processo.domain.status.ProcessoState;
import com.attus.sgpj.modules.processo.domain.status.ProcessoSuspenso;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
public class Processo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @NotNull
    @Column(unique = true)
    private String numero;

    @NotNull
    private String descricao;

    @NotNull
    private LocalDate dataAbertura;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatusProcessoEnum statusProcesso;

    @Transient
    private ProcessoState state;

    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL)
    private List<ParteEnvolvida> parteEnvolvidas = new ArrayList<>();

    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL)
    private List<Acao> acoes = new ArrayList<>();

    private Processo(String numero, String descricao, LocalDate dataAbertura) {
        this.numero = numero;
        this.descricao = descricao;
        this.dataAbertura = dataAbertura;
        this.statusProcesso = StatusProcessoEnum.ATIVO;
        this.state = new ProcessoAberto();
    }

    public static Processo create(String numero, String descricao) {
        return new Processo(numero, descricao, LocalDate.now());
    }

    public static Processo create(String numero, String descricao, LocalDate dataAbertura) {
        return new Processo(numero, descricao, dataAbertura);
    }

    @PostLoad
    @PostConstruct
    public void atualizarState() {
        switch (statusProcesso) {
            case ATIVO -> state = new ProcessoAberto();
            case SUSPENSO -> state = new ProcessoSuspenso();
            case ARQUIVADO -> state = new ProcessoArquivado();
        }
    }

    /// BEGIN REGRAS DE NEGOCIO ///
    public void ativar() {
        this.state.ativar(this);
        atualizarState();
    }

    public void suspender() {
        this.state.suspender(this);
        atualizarState();
    }

    public void arquivar() {
        this.state.arquivar(this);
        atualizarState();
    }

    /// END REGRAS DE NEGOCIO ///


    public void update(String numero, String descricao, LocalDate dataAbertura) {
        if (numero != null && !numero.isBlank()) {
            this.numero = numero;
        }
        if (descricao != null && !descricao.isBlank()) {
            this.descricao = descricao;
        }
        if (dataAbertura != null) {
            this.dataAbertura = dataAbertura;
        }
        // Não atualiza status diretamente aqui, use os métodos de negócio (ativar, suspender, arquivar)
    }

    public void addParte(ParteEnvolvida parte) {
        parte.setProcesso(this);
        parteEnvolvidas.add(parte);
    }

    public void removeParte(ParteEnvolvida parte) {
        this.parteEnvolvidas.remove(parte);
    }

    public void removeParteById(UUID parteId) {
        this.parteEnvolvidas.removeIf(p -> p.getId().equals(parteId));
    }

    public void adicionarAcao(Acao acao) {
        acao.setProcesso(this);
        acoes.add(acao);
    }

    public void removeAcao(Acao acao) {
        this.acoes.remove(acao);
    }

    public void removeAcaoById(UUID acaoId) {
        this.acoes.removeIf(a -> a.getId().equals(acaoId));
    }


    ///  VALIDACOES ///
    public boolean podeArquivar() {
        return temPartesObrigatorias(this.parteEnvolvidas)
                && temAcoesObrigatorias(this.acoes);
    }

    private boolean temPartesObrigatorias(List<ParteEnvolvida> partes) {
        boolean temAutor = partes.stream().anyMatch(p -> p.getTipo() == TipoParteEnvolvidaEnum.AUTOR);
        boolean temReu = partes.stream().anyMatch(p -> p.getTipo() == TipoParteEnvolvidaEnum.REU);
        boolean temAdvogado = partes.stream().anyMatch(p -> p.getTipo() == TipoParteEnvolvidaEnum.ADVOGADO);
        return temAutor && temReu && temAdvogado;
    }

    // TODO: Acao de Desistencia
    private boolean temAcoesObrigatorias(List<Acao> acoes) {
        boolean temPeticao = acoes.stream().anyMatch(a -> a.getTipo() == TipoAcaoEnum.PETICAO);
        boolean temAudiencia = acoes.stream().anyMatch(a -> a.getTipo() == TipoAcaoEnum.AUDIENCIA);
        boolean temSentenca = acoes.stream().anyMatch(a -> a.getTipo() == TipoAcaoEnum.SENTENCA);
        return temPeticao && temAudiencia && temSentenca;
    }
}

