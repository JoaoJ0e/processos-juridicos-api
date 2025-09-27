package com.attus.sgpj.shared.vo;

import com.attus.sgpj.shared.vo.Telefone;
import com.attus.sgpj.shared.vo.exception.InvalidFieldException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class TelefoneTest {

    @Test
    void deveCriarTelefoneComNumeroLimpo() {
        Telefone telefone = new Telefone("(11) 98765-4321");
        assertThat(telefone.getNumero()).isEqualTo("11987654321");
    }

    @Test
    void deveAceitarTelefoneCom10Digitos() {
        Telefone telefone = new Telefone("1187654321");
        assertThat(telefone.getNumero()).isEqualTo("1187654321");
        assertThat(telefone.formatado()).isEqualTo("(11) 8765-4321");
    }

    @Test
    void deveAceitarTelefoneCom11Digitos() {
        Telefone telefone = new Telefone("11987654321");
        assertThat(telefone.getNumero()).isEqualTo("11987654321");
        assertThat(telefone.formatado()).isEqualTo("(11) 98765-4321");
    }

    @Test
    void deveLancarExcecaoParaTelefoneInvalido() {
        assertThatThrownBy(() -> new Telefone("123"))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Telefone inválido.");
    }

    @Test
    void deveLancarExcecaoParaTelefoneNulo() {
        assertThatThrownBy(() -> new Telefone(null))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Telefone não pode ser nulo.");
    }

    @Test
    void toStringDeveRetornarTelefoneFormatado() {
        Telefone telefone = new Telefone("11987654321");
        assertThat(telefone.toString()).isEqualTo("(11) 98765-4321");
    }
}
