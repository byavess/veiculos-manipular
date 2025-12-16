package org.veiculo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    Map<String, String> err = new HashMap<>();
                    err.put("field", error.getField());
                    err.put("rejectedValue", String.valueOf(error.getRejectedValue()));
                    err.put("message", error.getDefaultMessage());
                    return err;
                })
                .collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("errors", errors);
        response.put("message", "Erro de validação nos campos do formulário");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

