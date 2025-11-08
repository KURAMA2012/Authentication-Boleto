package com.A3.projeto.Faculdade.AuthenticationBoleto.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tab_boleto")
public class Boleto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "codigo_barras")
	private String codigoBarras;

	@Column(name = "codigo_autenticacao")
	private String codigoAutenticacao;

	@Column(name = "data_geracao")
	private LocalDateTime dataGeracao;

	@Column(name = "autenticado")
	private boolean autenticado;

	@Column(name = "valor")
	private BigDecimal valor;

	@Column(name = "vencimento")
	private LocalDate vencimento;

	@Column(name = "assinatura_digital")
	private String assinaturaDigital;

	@Column(name = "cnpj_beneficiario")
	private String cnpjBeneficiario;

	@Column(name = "token_verificacao")
	private String tokenVerificacao;

	@Column(name = "nome_pagador")
	private String nomePagador;

	@Enumerated(EnumType.STRING)
	@Column(name = "status_boleto")
	private StatusBoleto status;

	@ManyToOne
	@JoinColumn(name = "id_empresa")
	private Empresa empresa;

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

	public LocalDateTime getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(LocalDateTime dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public boolean isAutenticado() {
		return autenticado;
	}

	public void setAutenticado(boolean autenticado) {
		this.autenticado = autenticado;
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

	public String getAssinaturaDigital() {
		return assinaturaDigital;
	}

	public void setAssinaturaDigital(String assinaturaDigital) {
		this.assinaturaDigital = assinaturaDigital;
	}

	public String getCnpjBeneficiario() {
		return cnpjBeneficiario;
	}

	public void setCnpjBeneficiario(String cnpjBeneficiario) {
		this.cnpjBeneficiario = cnpjBeneficiario;
	}

	public StatusBoleto getStatus() {
		return status;
	}

	public void setStatus(StatusBoleto status) {
		this.status = status;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public String getTokenVerificacao() {
		return tokenVerificacao;
	}

	public void setTokenVerificacao(String tokenVerificacao) {
		this.tokenVerificacao = tokenVerificacao;
	}

	public String getNomePagador() {
		return nomePagador;
	}

	public void setNomePagador(String nomePagador) {
		this.nomePagador = nomePagador;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Boleto other = (Boleto) obj;
		return Objects.equals(id, other.id);
	}

}