package com.A3.projeto.Faculdade.AuthenticationBoleto.exception;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ErrorCode;

/** Excecao de dominio que ja carrega o codigo estavel do contrato de erro. */
public class ApiException extends RuntimeException {
    private final ErrorCode code;

    public ApiException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() { return code; }
}