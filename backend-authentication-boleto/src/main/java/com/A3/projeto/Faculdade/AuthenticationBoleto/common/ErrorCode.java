package com.A3.projeto.Faculdade.AuthenticationBoleto.common;

import org.springframework.http.HttpStatus;

/** Codigos de erro estaveis: o front e os clientes devem depender do code, nunca da message. */
public enum ErrorCode {
    CSV_VAZIO(HttpStatus.BAD_REQUEST),
    CSV_CABECALHO_INVALIDO(HttpStatus.BAD_REQUEST),
    CSV_LINHA_INVALIDA(HttpStatus.UNPROCESSABLE_ENTITY),
    BOLETO_DUPLICADO(HttpStatus.CONFLICT),
    BOLETO_NAO_ENCONTRADO(HttpStatus.NOT_FOUND),
    EMPRESA_NAO_ENCONTRADA(HttpStatus.NOT_FOUND),
    VALIDACAO_CAMPO(HttpStatus.BAD_REQUEST),
    REGRA_NEGOCIO(HttpStatus.CONFLICT),
    CREDENCIAIS_INVALIDAS(HttpStatus.UNAUTHORIZED),
    TOKEN_INVALIDO(HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRADO(HttpStatus.UNAUTHORIZED),
    ACESSO_NEGADO(HttpStatus.FORBIDDEN),
    ERRO_INTERNO(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    ErrorCode(HttpStatus status) { this.status = status; }

    public HttpStatus getStatus() { return status; }
}