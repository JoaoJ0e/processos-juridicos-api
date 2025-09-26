package com.attus.sgpj.domain.dto;

import java.util.UUID;

public record PessoaResponseDTO(
        UUID id,
        String nomeCompleto,
        String cpfCnpj,
        String email,
        String telefone
) {}

