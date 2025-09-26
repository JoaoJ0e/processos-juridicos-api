package com.attus.sgpj.domain.model.processo;

import com.attus.sgpj.domain.model.acao.Acao;
import com.attus.sgpj.domain.model.parteenvolvida.ParteEnvolvida;
import com.attus.sgpj.domain.model.acao.TipoAcaoEnum;
import com.attus.sgpj.domain.model.parteenvolvida.TipoParteEnvolvidaEnum;
import com.attus.sgpj.domain.model.processo.status.ProcessoAberto;
import com.attus.sgpj.domain.model.processo.status.ProcessoArquivado;
import com.attus.sgpj.domain.model.processo.status.ProcessoState;
import com.attus.sgpj.domain.model.processo.status.ProcessoSuspenso;
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

    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParteEnvolvida> parteEnvolvidas = new ArrayList<>();

    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true) //TODO: Validar orphanRemoval
    private List<Acao> acoes = new ArrayList<>();

    public Processo(String numero, String descricao, LocalDate dataAbertura) {
        this.numero = numero;
        this.descricao = descricao;
        this.dataAbertura = dataAbertura;
        this.statusProcesso = StatusProcessoEnum.ATIVO;
        this.state = new ProcessoAberto();
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

    //Regras de Negócio
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


    // Validacoes
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

    private boolean temAcoesObrigatorias(List<Acao> acoes) {
        boolean temPeticao = acoes.stream().anyMatch(a -> a.getTipo() == TipoAcaoEnum.PETICAO);
        boolean temAudiencia = acoes.stream().anyMatch(a -> a.getTipo() == TipoAcaoEnum.AUDIENCIA);
        boolean temSentenca = acoes.stream().anyMatch(a -> a.getTipo() == TipoAcaoEnum.SENTENCA);
        return temPeticao && temAudiencia && temSentenca;
    }
}

