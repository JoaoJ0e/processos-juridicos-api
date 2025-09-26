package com.attus.sgpj.domain.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EmailTest {

    @Test
    void deveCriarEmailValido() {
        Email email = new Email("usuario@dominio.com");
        assertThat(email.getEmail()).isEqualTo("usuario@dominio.com");
    }

    @Test
    void deveConverterParaMinusculo() {
        Email email = new Email("UsuARio@Dominio.COM");
        assertThat(email.getEmail()).isEqualTo("usuario@dominio.com");
    }

    @Test
    void deveLancarExcecaoParaEmailNulo() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email não pode ser nulo.");
    }

    @Test
    void deveLancarExcecaoParaEmailInvalido() {
        assertThatThrownBy(() -> new Email("email-invalido"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email inválido.");
    }

    @Test
    void toStringDeveRetornarEmail() {
        Email email = new Email("teste@exemplo.com");
        assertThat(email.toString()).isEqualTo("teste@exemplo.com");
    }
}
