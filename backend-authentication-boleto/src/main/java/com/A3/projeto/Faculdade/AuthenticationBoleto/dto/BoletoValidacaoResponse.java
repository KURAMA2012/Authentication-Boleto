package com.A3.projeto.Faculdade.AuthenticationBoleto.dto;

public class BoletoValidacaoResponse {
	private boolean valido;
	private String mensagem;

	public BoletoValidacaoResponse(boolean valido, String mensagem) {
		this.valido = valido;
		this.mensagem = mensagem;
	}

	public boolean isValido() {
		return valido;
	}

	public String getMensagem() {
		return mensagem;
	}
}