package com.A3.projeto.Faculdade.AuthenticationBoleto.handler;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.BoletoNaoEncontradoException;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.EmpresaNaoEncontradaException;

/** Garante o contrato de erro: status HTTP + code estavel no corpo. */
class GlobalExceptionHandlerTest {

    @RestController
    static class FakeController {
        @GetMapping("/boleto") void boleto() { throw new BoletoNaoEncontradoException("Boleto não encontrado"); }
        @GetMapping("/empresa") void empresa() { throw new EmpresaNaoEncontradaException("Empresa não encontrada"); }
        @GetMapping("/regra") void regra() { throw new IllegalArgumentException("CNPJ já cadastrado"); }
        @GetMapping("/boom") void boom() { throw new IllegalStateException("detalhe interno sensivel"); }
    }

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new FakeController())
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void boletoNaoEncontradoRetorna404ComCode() throws Exception {
        mvc.perform(get("/boleto"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("BOLETO_NAO_ENCONTRADO"))
                .andExpect(jsonPath("$.message").value("Boleto não encontrado"));
    }

    @Test
    void empresaNaoEncontradaRetorna404ComCode() throws Exception {
        mvc.perform(get("/empresa"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EMPRESA_NAO_ENCONTRADA"));
    }

    @Test
    void regraDeNegocioRetorna409() throws Exception {
        mvc.perform(get("/regra"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("REGRA_NEGOCIO"));
    }

    @Test
    void erroInternoNaoVazaDetalheETemTraceId() throws Exception {
        mvc.perform(get("/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("ERRO_INTERNO"))
                .andExpect(jsonPath("$.traceId").isNotEmpty())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("sensivel"))));
    }
}