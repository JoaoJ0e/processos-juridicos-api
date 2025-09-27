package com.attus.sgpj.modules.pessoa.domain;


import com.attus.sgpj.modules.pessoa.domain.dto.PessoaRequestDTO;
import com.attus.sgpj.shared.vo.CpfCnpj;
import com.attus.sgpj.shared.vo.Email;
import com.attus.sgpj.shared.vo.Telefone;
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

    @Embedded
    @NotNull(message = "CPF/CNPJ é obrigatório")
    private CpfCnpj cpfCnpj;

    @Embedded
    @NotNull(message = "E-mail é obrigatório")
    private Email email;

    @Embedded
    @NotNull(message = "Telefone é obrigatório")
    private Telefone telefone;

    private Pessoa(String nomeCompleto, CpfCnpj cpfCnpj, Email email, Telefone telefone) {
        this.nomeCompleto = nomeCompleto;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.telefone = telefone;
    }

    public static Pessoa create(String nomeCompleto, String cpfCnpj, String email, String telefone) {
        return new Pessoa(nomeCompleto, new CpfCnpj(cpfCnpj), new Email(email), new Telefone(telefone));
    }

    public Pessoa update(PessoaRequestDTO dto) {
        this.nomeCompleto = dto.nomeCompleto();
        this.cpfCnpj = new CpfCnpj(dto.cpfCnpj());
        this.email = new Email(dto.email());
        this.telefone = new Telefone(dto.telefone());
        return this;
    }
}
