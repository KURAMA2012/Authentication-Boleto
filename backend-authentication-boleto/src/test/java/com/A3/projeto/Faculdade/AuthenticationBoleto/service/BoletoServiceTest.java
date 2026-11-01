package com.A3.projeto.Faculdade.AuthenticationBoleto.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ErrorCode;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoRequest;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoValidacaoResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Empresa;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.ApiException;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Boleto;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.StatusBoleto;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.StatusToken;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.TokenVerificacao;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.BoletoNaoEncontradoException;
import com.A3.projeto.Faculdade.AuthenticationBoleto.mapper.BoletoMapper;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.BoletoRepository;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.TokenVerificacaoRepository;

@ExtendWith(MockitoExtension.class)
class BoletoServiceTest {

    private static final String COD = "AUTH-123";
    private static final String EMAIL = "pagador@demo.local";
    private static final String CNPJ = "61099834000190";

    @Mock private BoletoRepository boletoRepository;
    @Mock private BoletoMapper boletoMapper;
    @Mock private TokenVerificacaoRepository tokenRepository;
    @Mock private EmpresaService empresaService;
    @Mock private TokenEmailService tokenEmailService;
    @Mock private BoletoLogService boletoLogService;
    @InjectMocks private BoletoService service;

    private Boleto boleto;

    @BeforeEach
    void setUp() {
        boleto = new Boleto();
        boleto.setCodigoAutenticacao(COD);
        boleto.setCodigoBarras("123");
        boleto.setStatus(StatusBoleto.GERADO);
    }

    private TokenVerificacao token(String valor, LocalDateTime expira) {
        return new TokenVerificacao(valor, LocalDateTime.now().minusMinutes(1), expira, boleto);
    }

    // ---------- confirmarToken ----------

    @Test
    @DisplayName("Token correto e dentro da validade autentica o boleto")
    void confirmarTokenValidoAutenticaBoleto() {
        TokenVerificacao t = token("123456", LocalDateTime.now().plusMinutes(5));
        when(boletoRepository.findByCodigoAutenticacao(COD)).thenReturn(boleto);
        when(tokenRepository.findByBoletoAndUtilizadoFalse(boleto)).thenReturn(Optional.of(t));

        assertThat(service.confirmarToken(COD, "123456")).isTrue();
        assertThat(t.isUtilizado()).isTrue();
        assertThat(t.getStatus()).isEqualTo(StatusToken.UTILIZADO);
        assertThat(boleto.getStatus()).isEqualTo(StatusBoleto.AUTENTICADO);
        assertThat(boleto.isAutenticado()).isTrue();
        verify(tokenRepository).save(t);
        verify(boletoRepository).save(boleto);
    }

    @Test
    @DisplayName("Token diferente do gerado é recusado e nada é salvo")
    void confirmarTokenErradoRetornaFalse() {
        when(boletoRepository.findByCodigoAutenticacao(COD)).thenReturn(boleto);
        when(tokenRepository.findByBoletoAndUtilizadoFalse(boleto))
                .thenReturn(Optional.of(token("123456", LocalDateTime.now().plusMinutes(5))));

        assertThat(service.confirmarToken(COD, "000000")).isFalse();
        assertThat(boleto.isAutenticado()).isFalse();
        verify(boletoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Token expirado é recusado")
    void confirmarTokenExpiradoRetornaFalse() {
        when(boletoRepository.findByCodigoAutenticacao(COD)).thenReturn(boleto);
        when(tokenRepository.findByBoletoAndUtilizadoFalse(boleto))
                .thenReturn(Optional.of(token("123456", LocalDateTime.now().minusSeconds(1))));

        assertThat(service.confirmarToken(COD, "123456")).isFalse();
        verify(boletoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Sem token ativo (já usado ou nunca gerado) é recusado")
    void confirmarSemTokenAtivoRetornaFalse() {
        when(boletoRepository.findByCodigoAutenticacao(COD)).thenReturn(boleto);
        when(tokenRepository.findByBoletoAndUtilizadoFalse(boleto)).thenReturn(Optional.empty());

        assertThat(service.confirmarToken(COD, "123456")).isFalse();
    }

    @Test
    @DisplayName("Confirmar token de boleto inexistente lança BoletoNaoEncontrado")
    void confirmarTokenBoletoInexistente() {
        when(boletoRepository.findByCodigoAutenticacao(COD)).thenReturn(null);

        assertThat(service.confirmarToken(COD, "123456")).isFalse();
    }

    // ---------- gerarOuRecuperarToken ----------

    @Test
    @DisplayName("Reaproveita o token ativo que ainda não expirou")
    void gerarTokenReutilizaTokenValido() {
        when(boletoRepository.findByCodigoAutenticacao(COD)).thenReturn(boleto);
        when(tokenRepository.findByBoletoAndUtilizadoFalse(boleto))
                .thenReturn(Optional.of(token("654321", LocalDateTime.now().plusMinutes(3))));

        assertThat(service.gerarOuRecuperarToken(COD, EMAIL)).isEqualTo("654321");
        verify(tokenRepository, never()).save(any());
        verify(tokenEmailService).enviarToken(EMAIL, COD, "654321"); // reenvia ao e-mail do pagador
    }

    @Test
    @DisplayName("Gera token novo de 6 dígitos com validade de 5 minutos quando não há ativo")
    void gerarTokenNovoQuandoNaoHaAtivo() {
        when(boletoRepository.findByCodigoAutenticacao(COD)).thenReturn(boleto);
        when(tokenRepository.findByBoletoAndUtilizadoFalse(boleto)).thenReturn(Optional.empty());

        String gerado = service.gerarOuRecuperarToken(COD, EMAIL);

        assertThat(gerado).matches("\\d{6}");
        ArgumentCaptor<TokenVerificacao> captor = ArgumentCaptor.forClass(TokenVerificacao.class);
        verify(tokenRepository).save(captor.capture());
        TokenVerificacao salvo = captor.getValue();
        assertThat(salvo.getToken()).isEqualTo(gerado);
        verify(tokenEmailService).enviarToken(eq(EMAIL), eq(COD), eq(gerado));
        assertThat(salvo.getStatus()).isEqualTo(StatusToken.ATIVO);
        assertThat(salvo.getDataExpiracao())
                .isAfter(LocalDateTime.now().plusMinutes(4))
                .isBefore(LocalDateTime.now().plusMinutes(6));
    }

    @Test
    @DisplayName("Gera token novo quando o ativo já expirou")
    void gerarTokenNovoQuandoAtivoExpirou() {
        when(boletoRepository.findByCodigoAutenticacao(COD)).thenReturn(boleto);
        when(tokenRepository.findByBoletoAndUtilizadoFalse(boleto))
                .thenReturn(Optional.of(token("111111", LocalDateTime.now().minusMinutes(1))));

        service.gerarOuRecuperarToken(COD, EMAIL);

        verify(tokenRepository).save(any(TokenVerificacao.class));
    }

    @Test
    @DisplayName("Gerar token para boleto inexistente lança BoletoNaoEncontrado")
    void gerarTokenBoletoInexistente() {
        when(boletoRepository.findByCodigoAutenticacao(COD)).thenReturn(null);

        assertThrows(BoletoNaoEncontradoException.class, () -> service.gerarOuRecuperarToken(COD, EMAIL));
    }

    // ---------- busca / criação ----------

    @Test
    @DisplayName("Buscar por código de autenticação inexistente lança BoletoNaoEncontrado")
    void buscarPorCodigoAutenticacaoInexistente() {
        when(boletoRepository.buscarPorCodigoAutenticacao(COD)).thenReturn(null);

        assertThrows(BoletoNaoEncontradoException.class, () -> service.buscarPorCodigoAutenticacao(COD));
    }

    private BoletoRequest reqVencendo(LocalDate venc) {
        BoletoRequest req = new BoletoRequest();
        req.setCodigoBarras("999");
        req.setCnpjBeneficiario(CNPJ);
        req.setVencimento(venc);
        return req;
    }

    private void stubCriacaoOk() {
        Empresa empresa = new Empresa();
        when(empresaService.buscarPorCnpj(CNPJ)).thenReturn(empresa);
        when(boletoMapper.toEntity(any())).thenReturn(new Boleto());
        when(boletoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    @DisplayName("Criar boleto com vencimento no passado: 400 VALIDACAO_CAMPO")
    void criarBoletoVencimentoPassadoRecusa() {
        ApiException ex = assertThrows(ApiException.class,
                () -> service.criarBoleto(reqVencendo(LocalDate.now().minusDays(1)), "dono@demo.local"));
        assertThat(ex.getCode()).isEqualTo(ErrorCode.VALIDACAO_CAMPO);
        verify(boletoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criar boleto vencendo hoje e aceito")
    void criarBoletoVencimentoHojeAceita() {
        stubCriacaoOk();
        assertThat(service.criarBoleto(reqVencendo(LocalDate.now()), "dono@demo.local")).isNotNull();
    }

    @Test
    @DisplayName("Criar boleto com vencimento futuro e aceito")
    void criarBoletoVencimentoFuturoAceita() {
        stubCriacaoOk();
        assertThat(service.criarBoleto(reqVencendo(LocalDate.now().plusDays(30)), "dono@demo.local")).isNotNull();
    }

    @Test
    @DisplayName("Criar boleto com c\u00f3digo de barras repetido \u00e9 recusado")
    void criarBoletoDuplicadoLancaExcecao() {
        BoletoRequest req = new BoletoRequest();
        req.setCodigoBarras("123");
        when(boletoRepository.buscarPorCodigoBarras("123")).thenReturn(boleto);

        assertThrows(IllegalArgumentException.class, () -> service.criarBoleto(req, "dono@demo.local"));
        verify(boletoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Boleto vindo do batch é salvo como GERADO com códigos gerados")
    void criarBoletoDoBatchPreencheCampos() {
        Boleto novo = new Boleto();
        novo.setCodigoBarras("999");
        when(boletoRepository.buscarPorCodigoBarras("999")).thenReturn(null);

        service.criarBoletoAPartirDoBatch(novo);

        assertThat(novo.getStatus()).isEqualTo(StatusBoleto.GERADO);
        assertThat(novo.getCodigoAutenticacao()).isNotBlank();
        assertThat(novo.getDataGeracao()).isNotNull();
        verify(boletoRepository).save(novo);
    }

    @Test
    @DisplayName("Boleto duplicado vindo do batch é ignorado")
    void criarBoletoDoBatchDuplicadoIgnora() {
        when(boletoRepository.buscarPorCodigoBarras("123")).thenReturn(boleto);

        service.criarBoletoAPartirDoBatch(boleto);

        verify(boletoRepository, never()).save(any());
    }

    // ---------- slice 1: tentativas, e-mail do pagador, validacao publica ----------

    @Test
    void confirmarTokenErradoNaQuintaVezBloqueiaCom429() {
        TokenVerificacao t = token("123456", LocalDateTime.now().plusMinutes(5));
        t.setTentativas(4);
        when(boletoRepository.findByCodigoAutenticacao(COD)).thenReturn(boleto);
        when(tokenRepository.findByBoletoAndUtilizadoFalse(boleto)).thenReturn(Optional.of(t));

        ApiException ex = assertThrows(ApiException.class, () -> service.confirmarToken(COD, "000000"));

        assertThat(ex.getCode()).isEqualTo(ErrorCode.LIMITE_TENTATIVAS);
        assertThat(t.isUtilizado()).isTrue();
        assertThat(t.getStatus()).isEqualTo(StatusToken.CANCELADO);
        verify(tokenRepository).save(t);
    }

    @Test
    void confirmarTokenErradoContaTentativa() {
        TokenVerificacao t = token("123456", LocalDateTime.now().plusMinutes(5));
        when(boletoRepository.findByCodigoAutenticacao(COD)).thenReturn(boleto);
        when(tokenRepository.findByBoletoAndUtilizadoFalse(boleto)).thenReturn(Optional.of(t));

        assertThat(service.confirmarToken(COD, "000000")).isFalse();
        assertThat(t.getTentativas()).isEqualTo(1);
        assertThat(t.isUtilizado()).isFalse();
    }

    @Test
    void enviarTokenSemEmailValidoRetorna400() {
        ApiException ex = assertThrows(ApiException.class, () -> service.gerarOuRecuperarToken(COD, "invalido"));
        assertThat(ex.getCode()).isEqualTo(ErrorCode.VALIDACAO_CAMPO);
    }

    @Test
    void validarPublicoCodigoInexistenteEhSuspeitoELogaCnpj() {
        when(boletoRepository.buscarPorCodigoAutenticacao(COD)).thenReturn(null);

        BoletoValidacaoResponse r = service.validarPublico(COD, "61.099.834/0001-90", "1.2.3.4");

        assertThat(r.getVeredito()).isEqualTo(BoletoValidacaoResponse.SUSPEITO);
        verify(boletoLogService).registrarLog(eq(COD), eq("1.2.3.4"), eq(false), any(), eq(null), eq(CNPJ));
    }

    @Test
    void validarPublicoCnpjDiferenteEhSuspeito() {
        Empresa e = new Empresa();
        e.setCnpj(CNPJ);
        boleto.setEmpresa(e);
        when(boletoRepository.buscarPorCodigoAutenticacao(COD)).thenReturn(boleto);

        BoletoValidacaoResponse r = service.validarPublico(COD, "11222333000181", "1.2.3.4");

        assertThat(r.getVeredito()).isEqualTo(BoletoValidacaoResponse.SUSPEITO);
        assertThat(boleto.isAutenticado()).isFalse();
    }

    @Test
    void validarPublicoCnpjCorretoEhAutentico() {
        Empresa e = new Empresa();
        e.setCnpj(CNPJ);
        boleto.setEmpresa(e);
        when(boletoRepository.buscarPorCodigoAutenticacao(COD)).thenReturn(boleto);

        BoletoValidacaoResponse r = service.validarPublico(COD, CNPJ, "1.2.3.4");

        assertThat(r.getVeredito()).isEqualTo(BoletoValidacaoResponse.AUTENTICO);
        verify(boletoLogService).registrarLog(eq(COD), eq("1.2.3.4"), eq(true), any(), eq(boleto), eq(CNPJ));
    }
}