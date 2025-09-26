package com.attus.sgpj.domain.vo;

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
    private String email;

    public Email(String email) {
        if (email == null) throw new IllegalArgumentException("Email não pode ser nulo.");
        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Email inválido.");
        }
        this.email = email.toLowerCase();
    }

    @Override
    public String toString() {
        return email;
    }
}
