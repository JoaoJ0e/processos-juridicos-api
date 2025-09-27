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
public class Email {

    @Getter
    @Column(name = "email")
    private String value;

    public Email(String value) {
        if (value == null) throw new InvalidFieldException("Email não pode ser nulo.");
        if (!value.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new InvalidFieldException("Email inválido.");
        }
        this.value = value.toLowerCase();
    }

    @Override
    public String toString() {
        return value;
    }
}
