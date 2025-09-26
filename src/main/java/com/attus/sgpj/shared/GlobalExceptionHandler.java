package com.attus.sgpj.shared;

import com.attus.sgpj.domain.exception.PessoaAlreadyExistsException;
import com.attus.sgpj.domain.exception.PessoaNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PessoaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePessoaNotFound(PessoaNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                "PESSOA_NOT_FOUND",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(PessoaAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePessoaAlreadyExists(PessoaAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(
                "PESSOA_ALREADY_EXISTS",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGenericError(Exception ex) {
//        ErrorResponse error = new ErrorResponse(
//                "INTERNAL_ERROR",
//                "Erro interno do servidor",
//                LocalDateTime.now()
//        );
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//    }

    // Error response records
    public record ErrorResponse(
            String code,
            String message,
            LocalDateTime timestamp
    ) {}

    public record ValidationErrorResponse(
            String code,
            String message,
            Map<String, String> fieldErrors,
            LocalDateTime timestamp
    ) {}
}