package com.a3.boleto.batch.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.a3.boleto.batch.client.BoletoApiClient;
import com.a3.boleto.batch.dto.BoletoItemRequest;
import com.a3.boleto.batch.dto.BoletoLoteRequest;
import com.a3.boleto.batch.dto.BoletoLoteResponse;

@Service
public class BoletoBatchService {

    private final BoletoApiClient boletoApiClient;

    public BoletoBatchService(BoletoApiClient boletoApiClient) {
        this.boletoApiClient = boletoApiClient;
    }

    public BoletoLoteResponse processarLote(BoletoLoteRequest request) {
        int inseridos = 0;
        int duplicados = 0;
        List<String> falhas = new ArrayList<>();

        for (BoletoItemRequest boleto : request.getBoletos()) {
            try {
                boletoApiClient.criarBoleto(boleto);
                inseridos++;
            } catch (Exception e) {
                String msg = e.getMessage();
                if (msg != null && msg.contains("409")) {
                    duplicados++;
                } else {
                    falhas.add("Erro com boleto " + boleto.getCodigoBarras() + ": " + msg);
                }
            }
        }

        return new BoletoLoteResponse(inseridos, duplicados, falhas);
    }
}