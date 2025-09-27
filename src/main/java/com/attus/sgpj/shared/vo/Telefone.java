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
public class Telefone {

    @Getter
    @Column(name = "telefone")
    private String numero;

    public Telefone(String numero) {
        if (numero == null) throw new InvalidFieldException("Telefone não pode ser nulo.");

        String numeroLimpo = limpar(numero);

        if (!numeroLimpo.matches("^\\d{10,11}$")) throw new InvalidFieldException("Telefone inválido.");

        this.numero = numeroLimpo;
    }

    private String limpar(String numero) {
        if (numero == null) return "";
        return numero.replaceAll("\\D", "");
    }

    public String formatado() {
        return switch (numero.length()) {
            case 10 -> String.format("(%s) %s-%s", numero.substring(0, 2), numero.substring(2, 6), numero.substring(6));
            case 11 -> String.format("(%s) %s-%s", numero.substring(0, 2), numero.substring(2, 7), numero.substring(7));
            default -> numero;
        };
    }

    @Override
    public String toString() {
        return formatado();
    }
}
