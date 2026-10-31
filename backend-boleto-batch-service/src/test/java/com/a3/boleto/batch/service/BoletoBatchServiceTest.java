package com.a3.boleto.batch.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.a3.boleto.batch.client.BoletoApiClient;
import com.a3.boleto.batch.dto.BoletoItemRequest;
import com.a3.boleto.batch.dto.BoletoLoteRequest;
import com.a3.boleto.batch.dto.BoletoLoteResponse;

class BoletoBatchServiceTest {

    private BoletoApiClient apiClient;
    private BoletoBatchService service;

    @BeforeEach
    void setUp() {
        apiClient = mock(BoletoApiClient.class);
        service = new BoletoBatchService(apiClient);
    }

    private static BoletoItemRequest boleto(String codigo) {
        BoletoItemRequest b = new BoletoItemRequest();
        b.setCodigoBarras(codigo);
        b.setValor(new BigDecimal("10.00"));
        b.setVencimento(LocalDate.of(2026, 12, 1));
        return b;
    }

    private static BoletoLoteRequest lote(BoletoItemRequest... itens) {
        BoletoLoteRequest r = new BoletoLoteRequest();
        r.setBoletos(List.of(itens));
        return r;
    }

    @Test
    @DisplayName("Conta todos como inseridos quando a API aceita todos")
    void deveContarInseridosQuandoTodosSaoAceitos() {
        BoletoLoteResponse resp = service.processarLote(lote(boleto("A"), boleto("B")));

        assertThat(resp.getBoletosInseridos()).isEqualTo(2);
        assertThat(resp.getBoletosDuplicados()).isZero();
        assertThat(resp.getFalhas()).isEmpty();
        verify(apiClient, times(2)).criarBoleto(any());
    }

    @Test
    @DisplayName("Conta como duplicado quando a API responde 409")
    void deveContarDuplicadoQuandoApiResponde409() {
        doThrow(new RuntimeException("[409] Conflict")).when(apiClient)
                .criarBoleto(argThat(b -> "DUP".equals(b.getCodigoBarras())));

        BoletoLoteResponse resp = service.processarLote(lote(boleto("OK"), boleto("DUP")));

        assertThat(resp.getBoletosInseridos()).isEqualTo(1);
        assertThat(resp.getBoletosDuplicados()).isEqualTo(1);
        assertThat(resp.getFalhas()).isEmpty();
    }

    @Test
    @DisplayName("Registra falha com o código de barras quando a API dá outro erro")
    void deveRegistrarFalhaParaOutrosErros() {
        doThrow(new RuntimeException("[500] Internal Server Error")).when(apiClient)
                .criarBoleto(argThat(b -> "ERR".equals(b.getCodigoBarras())));

        BoletoLoteResponse resp = service.processarLote(lote(boleto("ERR"), boleto("OK")));

        assertThat(resp.getBoletosInseridos()).isEqualTo(1);
        assertThat(resp.getBoletosDuplicados()).isZero();
        assertThat(resp.getFalhas()).singleElement().asString().contains("ERR").contains("500");
    }

    @Test
    @DisplayName("Lote vazio não chama a API")
    void loteVazioNaoChamaApi() {
        BoletoLoteResponse resp = service.processarLote(lote());

        assertThat(resp.getBoletosInseridos()).isZero();
        verify(apiClient, times(0)).criarBoleto(any());
    }
}