package com.attus.sgpj.shared.vo;

import com.attus.sgpj.shared.vo.exception.InvalidFieldException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CpfCnpjTest {

    @Test
    void deveCriarCpfCom11Digitos() {
        CpfCnpj cpf = new CpfCnpj("123.456.789-09");
        assertThat(cpf.getValue()).isEqualTo("12345678909");
    }

    @Test
    void deveCriarCnpjCom14Digitos() {
        CpfCnpj cnpj = new CpfCnpj("12.345.678/0001-95");
        assertThat(cnpj.getValue()).isEqualTo("12345678000195");
    }

    @Test
    void deveLancarExcecaoParaValorNulo() {
        assertThatThrownBy(() -> new CpfCnpj(null))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("CPF/CNPJ não pode ser nulo ou vazio.");
    }

    @Test
    void deveLancarExcecaoParaValorVazio() {
        assertThatThrownBy(() -> new CpfCnpj("   "))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("CPF/CNPJ não pode ser nulo ou vazio.");
    }

    @Test
    void deveLancarExcecaoParaQuantidadeDeDigitosInvalida() {
        assertThatThrownBy(() -> new CpfCnpj("12345"))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("CPF/CNPJ inválido: deve ter 11 ou 14 dígitos.");
    }

    @Test
    void toStringDeveRetornarValor() {
        CpfCnpj cpf = new CpfCnpj("12345678909");
        assertThat(cpf.toString()).hasToString("12345678909");
    }
}
