package com.a3.boleto.batch.dto;

import java.util.List;

public class BoletoLoteResponse {
	private int boletosInseridos;
	private int boletosDuplicados;
	private List<String> falhas;

	public BoletoLoteResponse(int boletosInseridos, int boletosDuplicados, List<String> falhas) {
		super();
		this.boletosInseridos = boletosInseridos;
		this.boletosDuplicados = boletosDuplicados;
		this.falhas = falhas;
	}

	public int getBoletosInseridos() {
		return boletosInseridos;
	}

	public void setBoletosInseridos(int boletosInseridos) {
		this.boletosInseridos = boletosInseridos;
	}

	public int getBoletosDuplicados() {
		return boletosDuplicados;
	}

	public void setBoletosDuplicados(int boletosDuplicados) {
		this.boletosDuplicados = boletosDuplicados;
	}

	public List<String> getFalhas() {
		return falhas;
	}

	public void setFalhas(List<String> falhas) {
		this.falhas = falhas;
	}

}