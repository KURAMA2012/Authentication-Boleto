package com.A3.projeto.Faculdade.AuthenticationBoleto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public class BoletoRequest {

    @NotBlank(message = "O código de barras é obrigatório")
    private String codigoBarras;

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "A data de vencimento é obrigatória")
    private LocalDate vencimento;

    @NotBlank(message = "O CNPJ da empresa é obrigatório")
    private String cnpjBeneficiario;

    @NotBlank(message = "O nome do pagador é obrigatório")
    private String nomePagador;
    
	private Long idEmpresa;

	

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

	public String getCodigoBarras() { 
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public Long getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Long idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
}
