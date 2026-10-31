package com.A3.projeto.Faculdade.AuthenticationBoleto.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Comportamento de seguranca de ponta a ponta (contexto real, perfil de teste com H2). */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired JwtService jwtService;

    @Test
    void rotaProtegidaSemTokenRetorna401ComTokenInvalido() throws Exception {
        mvc.perform(get("/api/empresas/listar"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("TOKEN_INVALIDO"));
    }

    @Test
    void tokenLixoRetorna401ComTokenInvalido() throws Exception {
        mvc.perform(get("/api/empresas/listar").header("Authorization", "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("TOKEN_INVALIDO"));
    }

    @Test
    void rotaProtegidaComTokenValidoPassa() throws Exception {
        String token = jwtService.gerarToken("davy@exemplo.com");
        mvc.perform(get("/api/empresas/listar").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void loginComCredencialErradaRetornaCodeNoCorpo() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/auth/login").param("email", "ninguem@x.com").param("senha", "errada"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("CREDENCIAIS_INVALIDAS"));
    }
}