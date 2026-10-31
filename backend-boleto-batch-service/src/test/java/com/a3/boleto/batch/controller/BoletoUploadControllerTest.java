package com.a3.boleto.batch.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.a3.boleto.batch.dto.BoletoItemRequest;
import com.a3.boleto.batch.producer.BoletoProducer;

class BoletoUploadControllerTest {

    private static final String HEADER =
            "codigoBarras,valor,dataVencimento,cnpjEmpresa,nomePagador,cpfCnpjPagador\n";

    private BoletoProducer producer;
    private BoletoUploadController controller;

    @BeforeEach
    void setUp() {
        producer = mock(BoletoProducer.class);
        controller = new BoletoUploadController(producer);
    }

    private static MockMultipartFile csv(String conteudo) {
        return new MockMultipartFile("file", "boletos.csv", "text/csv",
                conteudo.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("CSV válido: converte cada linha e envia para a fila")
    void csvValidoEnviaCadaLinhaParaFila() {
        String conteudo = HEADER
                + "123,150.75,2026-12-01,11222333000181,Maria,12345678909\n"
                + "456,20.00,2026-12-15,11222333000181,Joao,98765432100\n";

        ResponseEntity<?> resp = controller.uploadCsv(csv(conteudo));

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        ArgumentCaptor<BoletoItemRequest> captor = ArgumentCaptor.forClass(BoletoItemRequest.class);
        verify(producer, times(2)).enviarParaFila(captor.capture());
        BoletoItemRequest primeiro = captor.getAllValues().get(0);
        assertThat(primeiro.getCodigoBarras()).isEqualTo("123");
        assertThat(primeiro.getValor()).isEqualByComparingTo(new BigDecimal("150.75"));
        assertThat(primeiro.getVencimento()).isEqualTo(LocalDate.of(2026, 12, 1));
        assertThat(primeiro.getCnpjBeneficiario()).isEqualTo("11222333000181");
        assertThat(primeiro.getNomePagador()).isEqualTo("Maria");
        assertThat(primeiro.getCpfCnpjPagador()).isEqualTo("12345678909");
    }

    @Test
    @DisplayName("Cabeçalho sem diferenciar maiúsculas e valores com espaços são aceitos")
    void aceitaCabecalhoCaseInsensitiveETrim() {
        String conteudo = HEADER.toUpperCase()
                + " 789 , 5.00 , 2026-01-02 , 1 , Ana , 2 \n";

        ResponseEntity<?> resp = controller.uploadCsv(csv(conteudo));

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        ArgumentCaptor<BoletoItemRequest> captor = ArgumentCaptor.forClass(BoletoItemRequest.class);
        verify(producer).enviarParaFila(captor.capture());
        assertThat(captor.getValue().getCodigoBarras()).isEqualTo("789");
    }

    @Test
    @DisplayName("Arquivo vazio retorna 400 e não envia nada")
    void arquivoVazioRetorna400() {
        ResponseEntity<?> resp = controller.uploadCsv(csv(""));

        assertThat(resp.getStatusCode().value()).isEqualTo(400);
        verify(producer, never()).enviarParaFila(any());
    }

    @Test
    @DisplayName("Valor inválido retorna 500")
    void valorInvalidoRetorna500() {
        String conteudo = HEADER + "123,abc,2026-12-01,1,Maria,2\n";

        ResponseEntity<?> resp = controller.uploadCsv(csv(conteudo));

        assertThat(resp.getStatusCode().value()).isEqualTo(500);
        verify(producer, never()).enviarParaFila(any());
    }

    @Test
    @DisplayName("Data em formato errado retorna 500")
    void dataInvalidaRetorna500() {
        String conteudo = HEADER + "123,10.00,01/12/2026,1,Maria,2\n";

        ResponseEntity<?> resp = controller.uploadCsv(csv(conteudo));

        assertThat(resp.getStatusCode().value()).isEqualTo(500);
    }
}