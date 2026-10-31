package com.A3.projeto.Faculdade.AuthenticationBoleto.security;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ApiResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 401/403 no mesmo contrato de erro da API. */
@Component
public class JsonAuthEntryPoint implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException {
        Object attr = request.getAttribute(JwtAuthFilter.ATTR_ERRO);
        ErrorCode code = attr instanceof ErrorCode c ? c : ErrorCode.TOKEN_INVALIDO;
        String msg = code == ErrorCode.TOKEN_EXPIRADO ? "Sessão expirada. Faça login novamente." : "Token ausente ou inválido.";
        write(response, code, msg);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException {
        write(response, ErrorCode.ACESSO_NEGADO, "Acesso negado.");
    }

    private void write(HttpServletResponse response, ErrorCode code, String msg) throws IOException {
        response.setStatus(code.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getWriter(), ApiResponse.erro(code, msg, null, null));
    }
}