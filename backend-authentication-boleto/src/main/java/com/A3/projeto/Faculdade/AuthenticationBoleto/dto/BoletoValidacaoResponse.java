package com.A3.projeto.Faculdade.AuthenticationBoleto.dto;

/** Resposta publica da validacao: so o veredito e o motivo, sem dados do boleto. */
public class BoletoValidacaoResponse {

	public static final String AUTENTICO = "AUTENTICO";
	public static final String SUSPEITO = "SUSPEITO";

	private final String veredito;
	private final String motivo;

	public BoletoValidacaoResponse(String veredito, String motivo) {
		this.veredito = veredito;
		this.motivo = motivo;
	}

	public static BoletoValidacaoResponse autentico(String motivo) {
		return new BoletoValidacaoResponse(AUTENTICO, motivo);
	}

	public static BoletoValidacaoResponse suspeito(String motivo) {
		return new BoletoValidacaoResponse(SUSPEITO, motivo);
	}

	public String getVeredito() {
		return veredito;
	}

	public boolean isAutentico() {
		return AUTENTICO.equals(veredito);
	}

	public String getMotivo() {
		return motivo;
	}
}