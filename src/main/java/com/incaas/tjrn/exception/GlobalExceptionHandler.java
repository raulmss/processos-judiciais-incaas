package com.incaas.tjrn.exception;

import com.incaas.tjrn.exception.exceptions.ProcessoJudicialComNumeroJaExisteException;
import com.incaas.tjrn.exception.exceptions.RecursoNaoEncontradoException;
import com.incaas.tjrn.exception.exceptions.UserAlreadyExistsException;
import com.incaas.tjrn.exception.exceptions.ViolacaoDeRegraNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException; // Import this
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.validation.FieldError; // Import this for detailed field errors
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiError> handleNotFound(RecursoNaoEncontradoException ex, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getDescription(false));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ViolacaoDeRegraNegocioException.class)
    public ResponseEntity<ApiError> handleBusinessViolation(ViolacaoDeRegraNegocioException ex, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    @ExceptionHandler(ProcessoJudicialComNumeroJaExisteException.class)
    public ResponseEntity<ApiError> handleProcessoJudicialComNumeroJaExisteException(ProcessoJudicialComNumeroJaExisteException ex, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        String formattedMessage = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String field = error instanceof FieldError
                            ? ((FieldError) error).getField()
                            : error.getObjectName();
                    return field + ": " + error.getDefaultMessage();
                })
                .sorted()
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("Erro de validação");

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                formattedMessage,
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, WebRequest request) {
        String message = "Erro inesperado: " + ex.getClass().getSimpleName() + " - " + ex.getMessage();

        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                message,
                request.getDescription(false)
        );

        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}