package com.A3.projeto.Faculdade.AuthenticationBoleto.dto;

public class BoletoValidacaoRequest {
	private String codigoAutenticacao;
	/** Opcional em /validar: CNPJ do beneficiario impresso no boleto (com ou sem mascara). */
	private String cnpjInformado;
	/** Obrigatorio em /enviar-token: e-mail do pagador que recebe o token. */
	private String email;

	// Contrato do Front (/api/public/boleto): nomes novos; os antigos continuam aceitos.
	private String codigoBoleto;
	private String emailPagador;
	private String cnpjBeneficiario;

	public String getCodigoBoleto() { return codigoBoleto; }
	public void setCodigoBoleto(String v) { this.codigoBoleto = v; }
	public String getEmailPagador() { return emailPagador; }
	public void setEmailPagador(String v) { this.emailPagador = v; }
	public String getCnpjBeneficiario() { return cnpjBeneficiario; }
	public void setCnpjBeneficiario(String v) { this.cnpjBeneficiario = v; }

	public String codigo() { return codigoBoleto != null ? codigoBoleto : codigoAutenticacao; }
	public String emailDestino() { return emailPagador != null ? emailPagador : email; }
	public String cnpj() { return cnpjBeneficiario != null ? cnpjBeneficiario : cnpjInformado; }

	public String getCnpjInformado() {
		return cnpjInformado;
	}

	public void setCnpjInformado(String cnpjInformado) {
		this.cnpjInformado = cnpjInformado;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCodigoAutenticacao() {
		return codigoAutenticacao;
	}

	public void setCodigoAutenticacao(String codigoAutenticacao) {
		this.codigoAutenticacao = codigoAutenticacao;
	}
}