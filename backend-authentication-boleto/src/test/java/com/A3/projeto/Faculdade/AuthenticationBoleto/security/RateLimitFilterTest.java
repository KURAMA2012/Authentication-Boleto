package com.A3.projeto.Faculdade.AuthenticationBoleto.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RateLimitFilterTest {

    private final AtomicLong agora = new AtomicLong(1_000_000);
    private final RateLimitFilter filtro = new RateLimitFilter(3, 60, agora::get);

    private MockHttpServletResponse chamar(String metodo, String uri, String ip) throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(metodo, uri);
        req.setRemoteAddr(ip);
        MockHttpServletResponse res = new MockHttpServletResponse();
        filtro.doFilter(req, res, new MockFilterChain());
        return res;
    }

    @Test
    void excedeuLimiteRetorna429ComLimiteTentativas() throws Exception {
        for (int i = 0; i < 3; i++) {
            assertThat(chamar("POST", "/api/public/boleto/validar", "1.1.1.1").getStatus()).isEqualTo(200);
        }
        MockHttpServletResponse bloqueada = chamar("POST", "/api/public/boleto/validar", "1.1.1.1");
        assertThat(bloqueada.getStatus()).isEqualTo(429);
        assertThat(bloqueada.getContentAsString()).contains("\"code\":\"LIMITE_TENTATIVAS\"").contains("\"success\":false");
        assertThat(chamar("POST", "/api/public/boleto/validar", "2.2.2.2").getStatus()).isEqualTo(200);
    }

    @Test
    void novaJanelaLiberaDeNovo() throws Exception {
        for (int i = 0; i < 4; i++) chamar("POST", "/api/public/boleto/confirmar", "1.1.1.1");
        agora.addAndGet(60_000);
        assertThat(chamar("POST", "/api/public/boleto/confirmar", "1.1.1.1").getStatus()).isEqualTo(200);
    }

    @Test
    void rotasForaDoEscopoNaoSaoLimitadas() throws Exception {
        for (int i = 0; i < 10; i++) {
            assertThat(chamar("POST", "/api/auth/login", "1.1.1.1").getStatus()).isEqualTo(200);
            assertThat(chamar("GET", "/api/public/boleto/validar", "1.1.1.1").getStatus()).isEqualTo(200);
        }
    }
}