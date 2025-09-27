package com.attus.sgpj.modules.parteenvolvida.domain;

public enum TipoParteEnvolvidaEnum {
    AUTOR("Autor"),
    REU("Réu"),
    ADVOGADO("Advogado");

    private final String label;

    TipoParteEnvolvidaEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
