package com.a3.boleto.batch.dto;

import java.util.List;

public class BoletoLoteRequest {
	private Long idEmpresa;
	private List<BoletoItemRequest> boletos;

	public Long getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Long idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public List<BoletoItemRequest> getBoletos() {
		return boletos;
	}

	public void setBoletos(List<BoletoItemRequest> boletos) {
		this.boletos = boletos;
	}

}