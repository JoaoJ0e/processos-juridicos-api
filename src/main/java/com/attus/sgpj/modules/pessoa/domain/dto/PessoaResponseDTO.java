package com.attus.sgpj.modules.pessoa.domain.dto;

import com.attus.sgpj.modules.pessoa.domain.Pessoa;

import java.util.UUID;

public record PessoaResponseDTO(
        UUID id,
        String nomeCompleto,
        String cpfCnpj,
        String email,
        String telefone
) {
    public static PessoaResponseDTO fromDomain(Pessoa pessoa) {
        return new PessoaResponseDTO(
                pessoa.getId(),
                pessoa.getNomeCompleto(),
                pessoa.getCpfCnpj().getValue(),
                pessoa.getEmail().getValue(),
                pessoa.getTelefone().getNumero()
        );
    }
}
