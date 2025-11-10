package com.a3.boleto.batch.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BoletoItemRequest {

	private String codigoBarras;
	private BigDecimal valor;
	private LocalDate vencimento;
	private String CnpjBeneficiario;
	private String nomePagador;
	private String cpfCnpjPagador;

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public LocalDate getVencimento() {
		return vencimento;
	}

	public void setVencimento(LocalDate vencimento) {
		this.vencimento = vencimento;
	}

	public String getCnpjBeneficiario() {
		return CnpjBeneficiario;
	}

	public void setCnpjBeneficiario(String cnpjBeneficiario) {
		CnpjBeneficiario = cnpjBeneficiario;
	}

	public String getNomePagador() {
		return nomePagador;
	}

	public void setNomePagador(String nomePagador) {
		this.nomePagador = nomePagador;
	}

	public String getCpfCnpjPagador() {
		return cpfCnpjPagador;
	}

	public void setCpfCnpjPagador(String cpfCnpjPagador) {
		this.cpfCnpjPagador = cpfCnpjPagador;
	}

}