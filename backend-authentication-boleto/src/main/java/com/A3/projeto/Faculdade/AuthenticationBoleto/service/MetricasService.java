package com.A3.projeto.Faculdade.AuthenticationBoleto.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ErrorCode;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.EmpresaMetricasResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.BoletoLog;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Empresa;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.ApiException;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.BoletoLogRepository;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.BoletoRepository;

/**
 * Agrega os BoletoLog da empresa (logs dos boletos dela + logs com cnpjInformado = CNPJ dela).
 * Volume de demo: filtra/agrega em memoria; se crescer, trocar por GROUP BY no banco.
 */
@Service
public class MetricasService {

	private final EmpresaService empresaService;
	private final BoletoRepository boletoRepository;
	private final BoletoLogRepository boletoLogRepository;

	public MetricasService(EmpresaService empresaService, BoletoRepository boletoRepository,
			BoletoLogRepository boletoLogRepository) {
		this.empresaService = empresaService;
		this.boletoRepository = boletoRepository;
		this.boletoLogRepository = boletoLogRepository;
	}

	@Transactional(readOnly = true)
	public EmpresaMetricasResponse metricas(Long empresaId, String de, String ate, String emailLogado) {
		LocalDate inicio = data(de, "de");
		LocalDate fim = data(ate, "ate");
		if (inicio != null && fim != null && inicio.isAfter(fim)) {
			throw new ApiException(ErrorCode.VALIDACAO_CAMPO, "'de' n\u00e3o pode ser depois de 'ate'.");
		}
		Empresa empresa = empresaService.buscarPorId(empresaId);
		empresaService.verificarDono(empresa, emailLogado);

		long emitidos = boletoRepository.findByEmpresaId(empresaId).stream()
				.filter(b -> dentro(b.getDataGeracao() == null ? null : b.getDataGeracao().toLocalDate(), inicio, fim))
				.count();

		Map<LocalDate, long[]> porDia = new TreeMap<>();
		long autenticas = 0, suspeitas = 0;
		for (BoletoLog l : boletoLogRepository.findDaEmpresa(empresaId, empresa.getCnpj())) {
			LocalDate dia = l.getDataConsulta() == null ? null : l.getDataConsulta().toLocalDate();
			if (dia == null || !dentro(dia, inicio, fim)) continue;
			long[] c = porDia.computeIfAbsent(dia, k -> new long[2]);
			if (l.isValido()) { c[0]++; autenticas++; } else { c[1]++; suspeitas++; }
		}
		List<EmpresaMetricasResponse.Dia> dias = porDia.entrySet().stream()
				.map(e -> new EmpresaMetricasResponse.Dia(e.getKey(), e.getValue()[0], e.getValue()[1])).toList();
		return new EmpresaMetricasResponse(empresaId, inicio, fim, emitidos, autenticas, suspeitas, dias);
	}

	private static boolean dentro(LocalDate d, LocalDate inicio, LocalDate fim) {
		if (d == null) return inicio == null && fim == null;
		return (inicio == null || !d.isBefore(inicio)) && (fim == null || !d.isAfter(fim));
	}

	private static LocalDate data(String valor, String campo) {
		if (valor == null || valor.isBlank()) return null;
		try {
			return LocalDate.parse(valor.trim());
		} catch (DateTimeParseException e) {
			throw new ApiException(ErrorCode.VALIDACAO_CAMPO, "Par\u00e2metro '" + campo + "' deve ser uma data ISO (AAAA-MM-DD).");
		}
	}
}