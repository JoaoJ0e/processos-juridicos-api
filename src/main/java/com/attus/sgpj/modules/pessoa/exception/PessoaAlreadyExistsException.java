package com.attus.sgpj.modules.pessoa.exception;

public class PessoaAlreadyExistsException extends RuntimeException {
    public PessoaAlreadyExistsException(String message) {
        super(message);
    }
}