package com.A3.projeto.Faculdade.AuthenticationBoleto.dto;

import java.time.LocalDateTime;

import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.BoletoLog;

/** Linha da tela de Logs (mesmos campos de antes + cnpjInformado e idBoleto, sem a entidade inteira). */
public record BoletoLogResponse(Long id, String codigoAutenticacao, String ipUsuario, boolean valido, String mensagem,
		LocalDateTime dataConsulta, String cnpjInformado, Long idBoleto) {

	public static BoletoLogResponse de(BoletoLog l) {
		return new BoletoLogResponse(l.getId(), l.getCodigoAutenticacao(), l.getIpUsuario(), l.isValido(),
				l.getMensagem(), l.getDataConsulta(), l.getCnpjInformado(),
				l.getBoleto() != null ? l.getBoleto().getId() : null);
	}
}