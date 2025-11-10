package com.A3.projeto.Faculdade.AuthenticationBoleto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Boleto;

public class BoletoResponse {

	private Long id;
	private String codigoBarras; // ✅ padroniza nome
	private String codigoAutenticacao;
	private BigDecimal valor;
	private LocalDate vencimento;
	private String cnpjBeneficiario;
	private boolean autenticado;
	private LocalDateTime dataGeracao;
	private Long idEmpresa;

	public BoletoResponse() {
	}

	public BoletoResponse(Boleto boleto) {
	    this.id = boleto.getId();
	    this.codigoBarras = boleto.getCodigoBarras();
	    this.codigoAutenticacao = boleto.getCodigoAutenticacao();
	    this.valor = boleto.getValor();
	    this.vencimento = boleto.getVencimento();
	    this.cnpjBeneficiario = boleto.getCnpjBeneficiario();
	    this.autenticado = boleto.isAutenticado();
	    this.dataGeracao = boleto.getDataGeracao();
	    this.idEmpresa = boleto.getEmpresa() != null ? boleto.getEmpresa().getId() : null;
	}

	// ✅ Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public String getCodigoAutenticacao() {
		return codigoAutenticacao;
	}

	public void setCodigoAutenticacao(String codigoAutenticacao) {
		this.codigoAutenticacao = codigoAutenticacao;
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
		return cnpjBeneficiario;
	}

	public void setCnpjBeneficiario(String cnpjBeneficiario) {
		this.cnpjBeneficiario = cnpjBeneficiario;
	}

	public boolean isAutenticado() {
		return autenticado;
	}

	public void setAutenticado(boolean autenticado) {
		this.autenticado = autenticado;
	}

	public LocalDateTime getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(LocalDateTime dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public Long getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Long idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
}
