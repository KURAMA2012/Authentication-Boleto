package com.A3.projeto.Faculdade.AuthenticationBoleto.exception;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ErrorCode;

public class BoletoNaoEncontradoException extends ApiException {
    public BoletoNaoEncontradoException(String message) {
        super(ErrorCode.BOLETO_NAO_ENCONTRADO, message);
    }
}