package com.A3.projeto.Faculdade.AuthenticationBoleto.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ApiResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoRequest;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Boleto;
import com.A3.projeto.Faculdade.AuthenticationBoleto.service.BoletoService;

@RestController
@RequestMapping("api/boleto")
public class BoletoController {

	@Autowired
	private BoletoService boletoService;

	/** JWT: so cria boleto para empresa do usuario logado (403 ACESSO_NEGADO). */
	@PostMapping("/criar")
	public ResponseEntity<ApiResponse<BoletoResponse>> criar(@RequestBody BoletoRequest request, Principal principal) {
		Boleto boleto = boletoService.criarBoleto(request, principal.getName());
		return ResponseEntity.ok(new ApiResponse<>(true, "Boleto criado com sucesso", new BoletoResponse(boleto)));
	}

	/** JWT: so o dono da empresa do boleto (403 ACESSO_NEGADO). */
	@GetMapping("/buscar/{codigoBarras}")
	public ResponseEntity<ApiResponse<BoletoResponse>> buscarPorCodigoBarras(@PathVariable String codigoBarras,
			Principal principal) {
		Boleto boleto = boletoService.buscarPorCodigoBarras(codigoBarras);
		boletoService.verificarDono(boleto, principal.getName());
		return ResponseEntity.ok(new ApiResponse<>(true, "Boleto encontrado", new BoletoResponse(boleto)));
	}

	// Rotas publicas do pagador (validar, enviar-token, confirmar): ver PublicBoletoController,
	// que atende /api/public/boleto/* e tambem os caminhos antigos /api/boleto/*.
}
