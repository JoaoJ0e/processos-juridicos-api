package com.attus.sgpj.modules.processo.exception;

public class ProcessoInvalidStateTransitionException extends RuntimeException {
    public ProcessoInvalidStateTransitionException(String message) {
        super(message);
    }
}
