package com.A3.projeto.Faculdade.AuthenticationBoleto.exception;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ErrorCode;

public class EmpresaNaoEncontradaException extends ApiException {
    public EmpresaNaoEncontradaException(String message) {
        super(ErrorCode.EMPRESA_NAO_ENCONTRADA, message);
    }
}