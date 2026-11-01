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
    @Autowired com.A3.projeto.Faculdade.AuthenticationBoleto.repository.UsuarioRepository usuarioRepository;

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
        if (usuarioRepository.findByEmail("davy@exemplo.com").isEmpty()) {
            usuarioRepository.save(new com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Usuario("Usuario Teste", "davy@exemplo.com", "x", null, null));
        }
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

    @Test
    void validarPublicoSemTokenRetorna200Suspeito() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/public/boleto/validar")
                        .with(r -> { r.setRemoteAddr("10.0.0.1"); return r; })
                        .contentType("application/json")
                        .content("{\"codigoBoleto\":\"NAO-EXISTE-123\",\"emailPagador\":\"pagador@demo.local\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.veredito").value("SUSPEITO"))
                .andExpect(jsonPath("$.data.autentico").value(false))
                .andExpect(jsonPath("$.data.motivo").exists());
    }

    @Test
    void caminhoAntigoValidarContinuaPublico() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/boleto/validar")
                        .with(r -> { r.setRemoteAddr("10.0.0.2"); return r; })
                        .contentType("application/json").content("{\"codigoAutenticacao\":\"NAO-EXISTE-456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.veredito").value("SUSPEITO"));
    }

    @Test
    void logsSemTokenRetorna401() throws Exception {
        mvc.perform(get("/api/logs")).andExpect(status().isUnauthorized());
    }
}