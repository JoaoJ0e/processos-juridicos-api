package com.attus.sgpj.modules.acao.domain;

public enum TipoAcaoEnum {
    PETICAO("Petição"),
    AUDIENCIA("Audiência"),
    SENTENCA("Sentença"),
    DESISTENCIA("Desistência");

    private final String label;

    TipoAcaoEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
