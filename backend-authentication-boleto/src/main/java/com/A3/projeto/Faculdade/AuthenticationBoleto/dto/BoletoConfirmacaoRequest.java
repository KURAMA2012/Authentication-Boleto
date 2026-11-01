package com.A3.projeto.Faculdade.AuthenticationBoleto.dto;

public class BoletoConfirmacaoRequest {
	private String codigoAutenticacao;
	private String tokenVerificacao;
	private String codigoBoleto;
	private String token;

	public String getCodigoBoleto() { return codigoBoleto; }
	public void setCodigoBoleto(String v) { this.codigoBoleto = v; }
	public String getToken() { return token; }
	public void setToken(String v) { this.token = v; }

	public String codigo() { return codigoBoleto != null ? codigoBoleto : codigoAutenticacao; }
	public String tokenInformado() { return token != null ? token : tokenVerificacao; }

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
