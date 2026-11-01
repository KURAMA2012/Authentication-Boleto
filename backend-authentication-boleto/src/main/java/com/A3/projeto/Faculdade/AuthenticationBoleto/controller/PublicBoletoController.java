package com.A3.projeto.Faculdade.AuthenticationBoleto.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ApiResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ErrorCode;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.ApiException;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoConfirmacaoRequest;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoValidacaoRequest;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoValidacaoResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.service.BoletoService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Fluxo PUBLICO do pagador (sem JWT, com rate limit por IP). Caminho oficial /api/public/boleto/*;
 * /api/boleto/* continua aceito por compatibilidade. codigoBoleto aceita o codigo de autenticacao
 * ou o codigo de barras.
 */
@RestController
@RequestMapping({"/api/public/boleto", "/api/boleto"})
public class PublicBoletoController {

	private final BoletoService boletoService;

	public PublicBoletoController(BoletoService boletoService) {
		this.boletoService = boletoService;
	}

	/** Sempre 200: {autentico, veredito, motivo, tokenEnviado}. Se AUTENTICO e houver emailPagador, ja envia o token. */
	@PostMapping("/validar")
	public ResponseEntity<ApiResponse<Map<String, Object>>> validar(@RequestBody BoletoValidacaoRequest request,
			HttpServletRequest servletRequest) {
		exigirCodigo(request.codigo());
		String codigo = boletoService.resolverCodigoAutenticacao(request.codigo());
		BoletoValidacaoResponse r = boletoService.validarPublico(codigo, request.cnpj(), servletRequest.getRemoteAddr());
		boolean tokenEnviado = false;
		if (r.isAutentico() && request.emailDestino() != null && !request.emailDestino().isBlank()) {
			boletoService.gerarOuRecuperarToken(codigo, request.emailDestino());
			tokenEnviado = true;
		}
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("autentico", r.isAutentico());
		data.put("veredito", r.getVeredito());
		data.put("motivo", r.getMotivo());
		data.put("tokenEnviado", tokenEnviado);
		return ResponseEntity.ok(new ApiResponse<>(true, r.isAutentico() ? "Boleto aut\u00eantico." : "Boleto suspeito.", data));
	}

	/** Body {codigoBoleto, emailPagador}: (re)envia o token ao e-mail do pagador. */
	@PostMapping("/enviar-token")
	public ResponseEntity<ApiResponse<String>> enviarToken(@RequestBody BoletoValidacaoRequest request) {
		exigirCodigo(request.codigo());
		boletoService.gerarOuRecuperarToken(boletoService.resolverCodigoAutenticacao(request.codigo()), request.emailDestino());
		return ResponseEntity.ok(new ApiResponse<>(true, "Token de verifica\u00e7\u00e3o enviado por e-mail."));
	}

	/** Body {codigoBoleto, token}. Errado: 401 TOKEN_INVALIDO; na 5a tentativa errada: 429 LIMITE_TENTATIVAS. */
	private static void exigirCodigo(String codigo) {
		if (codigo == null || codigo.isBlank()) {
			throw new ApiException(ErrorCode.VALIDACAO_CAMPO, "Informe o c\u00f3digo do boleto (codigoBoleto).");
		}
	}

	@PostMapping("/confirmar")
	public ResponseEntity<ApiResponse<String>> confirmar(@RequestBody BoletoConfirmacaoRequest request) {
		boolean ok = boletoService.confirmarToken(boletoService.resolverCodigoAutenticacao(request.codigo()), request.tokenInformado());
		if (!ok) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.erro(ErrorCode.TOKEN_INVALIDO,
					"Token inv\u00e1lido ou expirado.", null, null));
		}
		return ResponseEntity.ok(new ApiResponse<>(true, "Boleto confirmado com sucesso! Pagamento pode prosseguir."));
	}
}