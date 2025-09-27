package com.attus.sgpj.modules.processo.exception;

public class ProcessoAlreadyExistsException extends RuntimeException {
    public ProcessoAlreadyExistsException(String message) {
        super(message);
    }
}
