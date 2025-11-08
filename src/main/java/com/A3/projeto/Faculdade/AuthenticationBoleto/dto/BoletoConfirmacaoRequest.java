package com.A3.projeto.Faculdade.AuthenticationBoleto.dto;

public class BoletoConfirmacaoRequest {
	private String codigoAutenticacao;
	private String tokenVerificacao;

	public String getCodigoAutenticacao() {
		return codigoAutenticacao;
	}

	public void setCodigoAutenticacao(String codigoAutenticacao) {
		this.codigoAutenticacao = codigoAutenticacao;
	}

	public String getTokenVerificacao() {
		return tokenVerificacao;
	}

	public void setTokenVerificacao(String tokenVerificacao) {
		this.tokenVerificacao = tokenVerificacao;
	}
}
