package com.A3.projeto.Faculdade.AuthenticationBoleto.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ErrorCode;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.ApiException;

class JwtServiceTest {

    private static final String SEGREDO = "segredo-de-teste-com-pelo-menos-32-caracteres!!";

    @Test
    void geraEValidaToken() {
        JwtService jwt = new JwtService(SEGREDO, 60_000);
        assertThat(jwt.validar(jwt.gerarToken("davy@exemplo.com"))).isEqualTo("davy@exemplo.com");
    }

    @Test
    void tokenExpiradoRetornaTokenExpirado() {
        JwtService jwt = new JwtService(SEGREDO, -1_000);
        String token = jwt.gerarToken("davy@exemplo.com");
        assertThatThrownBy(() -> jwt.validar(token))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getCode()).isEqualTo(ErrorCode.TOKEN_EXPIRADO);
    }

    @Test
    void tokenAssinadoComOutraChaveEInvalido() {
        String token = new JwtService("outro-segredo-totalmente-diferente-32+chars", 60_000).gerarToken("x@y.com");
        assertThatThrownBy(() -> new JwtService(SEGREDO, 60_000).validar(token))
                .extracting(e -> ((ApiException) e).getCode()).isEqualTo(ErrorCode.TOKEN_INVALIDO);
    }

    @Test
    void segredoCurtoFalhaNaSubida() {
        assertThatThrownBy(() -> new JwtService("curto", 60_000)).isInstanceOf(IllegalStateException.class);
    }
}