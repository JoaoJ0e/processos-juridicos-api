package com.attus.sgpj.shared;

import com.attus.sgpj.modules.pessoa.exception.PessoaAlreadyExistsException;
import com.attus.sgpj.modules.pessoa.exception.PessoaNotFoundException;
import com.attus.sgpj.modules.processo.exception.ProcessoAlreadyExistsException;
import com.attus.sgpj.modules.processo.exception.ProcessoCannotBeArchivedException;
import com.attus.sgpj.modules.processo.exception.ProcessoInvalidStateTransitionException;
import com.attus.sgpj.modules.processo.exception.ProcessoNotFoundException;
import com.attus.sgpj.shared.vo.exception.InvalidFieldException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /// BEGIN PESSOA ///
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
    /// END PESSOA ///

    // BEGIN PROCESSO ///
    @ExceptionHandler(ProcessoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProcessoNotFound(ProcessoNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                "PROCESSO_NOT_FOUND",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ProcessoAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleProcessoAlreadyExists(ProcessoAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(
                "PROCESSO_ALREADY_EXISTS",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ProcessoCannotBeArchivedException.class)
    public ResponseEntity<ErrorResponse> handleProcessoCannotBeArchived(ProcessoCannotBeArchivedException ex) {
        ErrorResponse error = new ErrorResponse(
                "PROCESSO_CANNOT_BE_ARCHIVED",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ProcessoInvalidStateTransitionException.class)
    public ResponseEntity<ErrorResponse> handleProcessoInvalidStateTransition(ProcessoInvalidStateTransitionException ex) {
        ErrorResponse error = new ErrorResponse(
                "PROCESSO_INVALID_STATE_TRANSITION",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    /// END PROCESSO ///

    /// BEGIN VO ///
    @ExceptionHandler(InvalidFieldException.class)
    public ResponseEntity<ErrorResponse> handleInvalidField(InvalidFieldException ex) {
        ErrorResponse error = new ErrorResponse(
                "CAMPO_INVALIDO",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
        /// END VO ///

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