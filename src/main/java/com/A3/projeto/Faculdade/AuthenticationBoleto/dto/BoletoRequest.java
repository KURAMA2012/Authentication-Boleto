package com.A3.projeto.Faculdade.AuthenticationBoleto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
public class BoletoRequest {

	private String cnpjEmpresa;
	private String nomePagador;
	private BigDecimal valor;
	private LocalDate dataVencimento;
	private String descricao;
	private String codigoBarras; // ✅ padroniza com o Response
	private Long idEmpresa;

	public String getCnpjEmpresa() {
		return cnpjEmpresa;
	}

	public void setCnpjEmpresa(String cnpjEmpresa) {
		this.cnpjEmpresa = cnpjEmpresa;
	}

	public String getNomePagador() {
		return nomePagador;
	}

	public void setNomePagador(String nomePagador) {
		this.nomePagador = nomePagador;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public LocalDate getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(LocalDate dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getCodigoBarras() { // ✅
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) { // ✅
		this.codigoBarras = codigoBarras;
	}

	public Long getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Long idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
}
