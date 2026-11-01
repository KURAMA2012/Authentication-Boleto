package com.A3.projeto.Faculdade.AuthenticationBoleto.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ErrorCode;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Boleto;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.BoletoLog;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Empresa;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.ApiException;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.BoletoLogRepository;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.BoletoRepository;

@ExtendWith(MockitoExtension.class)
class MetricasServiceTest {

    private static final String CNPJ = "61099834000190";
    private static final String EMAIL = "dono@demo.local";

    @Mock private EmpresaService empresaService;
    @Mock private BoletoRepository boletoRepository;
    @Mock private BoletoLogRepository boletoLogRepository;
    @InjectMocks private MetricasService service;

    private Empresa empresa() {
        Empresa e = new Empresa();
        e.setId(7L);
        e.setCnpj(CNPJ);
        when(empresaService.buscarPorId(7L)).thenReturn(e);
        return e;
    }

    private static BoletoLog log(boolean valido, String quando) {
        BoletoLog l = new BoletoLog();
        l.setValido(valido);
        l.setDataConsulta(LocalDateTime.parse(quando));
        return l;
    }

    private static Boleto boleto(String gerado) {
        Boleto b = new Boleto();
        b.setDataGeracao(LocalDateTime.parse(gerado));
        return b;
    }

    @Test
    void agregaTotaisEPorDiaDentroDoPeriodo() {
        empresa();
        when(boletoRepository.findByEmpresaId(7L)).thenReturn(List.of(boleto("2026-10-01T10:00:00"), boleto("2026-10-20T10:00:00")));
        when(boletoLogRepository.findDaEmpresa(7L, CNPJ)).thenReturn(List.of(
                log(true, "2026-10-02T09:00:00"), log(false, "2026-10-02T18:00:00"),
                log(false, "2026-10-03T08:00:00"), log(true, "2026-09-30T23:59:00")));

        var r = service.metricas(7L, "2026-10-01", "2026-10-31", EMAIL);

        assertThat(r.boletosEmitidos()).isEqualTo(2);
        assertThat(r.validacoesAutenticas()).isEqualTo(1);
        assertThat(r.validacoesSuspeitas()).isEqualTo(2);
        assertThat(r.porDia()).containsExactly(
                new com.A3.projeto.Faculdade.AuthenticationBoleto.dto.EmpresaMetricasResponse.Dia(LocalDate.of(2026, 10, 2), 1, 1),
                new com.A3.projeto.Faculdade.AuthenticationBoleto.dto.EmpresaMetricasResponse.Dia(LocalDate.of(2026, 10, 3), 0, 1));
    }

    @Test
    void semPeriodoContaTudo() {
        empresa();
        when(boletoRepository.findByEmpresaId(7L)).thenReturn(List.of(boleto("2020-01-01T00:00:00")));
        when(boletoLogRepository.findDaEmpresa(7L, CNPJ)).thenReturn(List.of(log(true, "2020-01-01T00:00:00")));

        var r = service.metricas(7L, null, null, EMAIL);

        assertThat(r.boletosEmitidos()).isEqualTo(1);
        assertThat(r.validacoesAutenticas()).isEqualTo(1);
    }

    @Test
    void outroDonoRecebe403() {
        Empresa e = empresa();
        doThrow(new ApiException(ErrorCode.ACESSO_NEGADO, "x")).when(empresaService).verificarDono(e, "outro@demo.local");

        assertThatThrownBy(() -> service.metricas(7L, null, null, "outro@demo.local"))
                .extracting(ex -> ((ApiException) ex).getCode()).isEqualTo(ErrorCode.ACESSO_NEGADO);
        verifyNoInteractions(boletoLogRepository);
    }

    @Test
    void dataInvalidaRetorna400() {
        assertThatThrownBy(() -> service.metricas(7L, "01/10/2026", null, EMAIL))
                .extracting(ex -> ((ApiException) ex).getCode()).isEqualTo(ErrorCode.VALIDACAO_CAMPO);
        verifyNoInteractions(empresaService);
    }
}