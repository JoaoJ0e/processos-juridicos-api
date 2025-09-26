package com.attus.sgpj.domain.model.pessoa;


import com.attus.sgpj.domain.dto.PessoaRequestDTO;
import com.attus.sgpj.domain.vo.Email;
import com.attus.sgpj.domain.vo.Telefone;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Entity
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @NotNull(message = "Nome completo é obrigatório")
    private String nomeCompleto;

    @NotNull(message = "CPF/CNPJ é obrigatório")
    private String cpfCnpj;

    @Embedded
    @NotNull(message = "E-mail é obrigatório")
    private Email email;

    @Embedded
    @NotNull(message = "Telefone é obrigatório")
    private Telefone telefone;

    private Pessoa(String nomeCompleto, String cpfCnpj, Email email, Telefone telefone) {
        this.nomeCompleto = nomeCompleto;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.telefone = telefone;
    }

    public static Pessoa create(String nomeCompleto, String cpfCnpj, String email, String telefone) { //TODO: Adicionar @NotNulls
        return new Pessoa(nomeCompleto, cpfCnpj, new Email(email), new Telefone(telefone));
    }

    public Pessoa update(PessoaRequestDTO dto) {
        this.nomeCompleto = dto.nomeCompleto();
        this.cpfCnpj = dto.cpfCnpj();
        this.email = new Email(dto.email());
        this.telefone = new Telefone(dto.telefone());
        return this;
    }
}
