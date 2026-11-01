package com.A3.projeto.Faculdade.AuthenticationBoleto.dto;

import java.time.LocalDate;
import java.util.List;

/** Metricas do dashboard da empresa (GET /api/empresas/{id}/metricas). */
public record EmpresaMetricasResponse(Long empresaId, LocalDate de, LocalDate ate, long boletosEmitidos,
		long validacoesAutenticas, long validacoesSuspeitas, List<Dia> porDia) {

	public record Dia(LocalDate data, long autenticas, long suspeitas) {
	}
}