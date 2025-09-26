package com.attus.sgpj.domain.model.parteenvolvida;

import com.attus.sgpj.domain.model.pessoa.Pessoa;
import com.attus.sgpj.domain.model.processo.Processo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@SQLDelete(sql = "UPDATE acao SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class ParteEnvolvida {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(optional = false)
    private Pessoa pessoa;

    @ManyToOne(optional = false)
    @JoinColumn(name = "processo_id")
    private Processo processo;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoParteEnvolvidaEnum tipo;

    @NotNull
    private boolean deleted;

    private ParteEnvolvida(Pessoa pessoa, Processo processo, TipoParteEnvolvidaEnum tipo) {
        this.pessoa = pessoa;
        this.processo = processo;
        this.tipo = tipo;
        this.deleted = false;
    }

    public static ParteEnvolvida create(Pessoa pessoa, Processo processo, TipoParteEnvolvidaEnum tipo) { //TODO: Adicionar @NotNulls
        return new ParteEnvolvida(pessoa, processo, tipo);
    }

    public void inativar() {
        this.deleted = true;
    }

    public void ativar() {
        this.deleted = false;
    }
}
