package com.attus.sgpj.shared.vo;

import com.attus.sgpj.shared.vo.exception.InvalidFieldException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Embeddable
public class CpfCnpj {

    @Getter
    @Column(name = "cpf_cnpj", unique = true, nullable = false)
    private String value;

    public CpfCnpj(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidFieldException("CPF/CNPJ não pode ser nulo ou vazio.");
        }

        String digits = value.replaceAll("\\D", "");

        if (digits.length() != 11 && digits.length() != 14) {
            throw new InvalidFieldException("CPF/CNPJ inválido: deve ter 11 ou 14 dígitos.");
        }

        this.value = digits;
    }

    @Override
    public String toString() {
        return value;
    }
}
