package com.A3.projeto.Faculdade.AuthenticationBoleto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Boleto;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.BoletoLog;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.BoletoLogRepository;

@Service
public class BoletoLogService {

    @Autowired
    private BoletoLogRepository boletoLogRepository;

    public void registrarLog(String codigoAutenticacao, String ipUsuario, boolean valido, String mensagem, Boleto boleto) {
        registrarLog(codigoAutenticacao, ipUsuario, valido, mensagem, boleto, null);
    }

    public void registrarLog(String codigoAutenticacao, String ipUsuario, boolean valido, String mensagem, Boleto boleto,
                             String cnpjInformado) {
        BoletoLog log = new BoletoLog(codigoAutenticacao, ipUsuario, valido, mensagem, boleto);
        log.setCnpjInformado(cnpjInformado);
        boletoLogRepository.save(log);
    }
}