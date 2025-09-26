package com.attus.sgpj.domain.exception;

public class PessoaAlreadyExistsException extends RuntimeException {
    public PessoaAlreadyExistsException(String message) {
        super(message);
    }
}