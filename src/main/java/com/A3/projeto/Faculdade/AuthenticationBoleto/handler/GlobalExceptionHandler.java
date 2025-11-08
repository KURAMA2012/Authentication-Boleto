package com.A3.projeto.Faculdade.AuthenticationBoleto.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.BoletoNaoEncontradoException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BoletoNaoEncontradoException.class)
    public ResponseEntity<String> handleBoletoNaoEncontrado(BoletoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}