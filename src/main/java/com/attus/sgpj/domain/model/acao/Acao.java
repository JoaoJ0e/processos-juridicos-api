package com.attus.sgpj.domain.model.acao;

import com.attus.sgpj.domain.model.processo.Processo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@SQLDelete(sql = "UPDATE acao SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Acao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include

    private UUID id;

    @NotNull(message = "Tipo Acao é obrigatório")
    @Enumerated(EnumType.STRING)
    private TipoAcaoEnum tipo;

    private String descricao;

    @NotNull
    private LocalDateTime dataRegistro;

    @ManyToOne
    @JoinColumn(name = "processo_id")
    private Processo processo;

    @NotNull
    private boolean deleted;

    protected Acao(@NotNull(message = "Tipo Acao é obrigatório") TipoAcaoEnum tipo, String descricao, Processo processo) {
        this.tipo = tipo;
        this.descricao = descricao;
        this.processo = processo;
        this.dataRegistro = LocalDateTime.now();
        this.deleted = false;
    }

    public static Acao create(@NotNull(message = "Tipo Acao é obrigatório") TipoAcaoEnum tipoAcao, String descricao, Processo processo) { //TODO: Adicionar @NotNulls
        return new Acao(tipoAcao, descricao, processo);
    }

    public void inativar() {
        this.deleted = true;
    }

    public void ativar() {
        this.deleted = false;
    }
}
