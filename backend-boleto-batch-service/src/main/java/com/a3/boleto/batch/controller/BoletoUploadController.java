package com.a3.boleto.batch.controller;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.a3.boleto.batch.dto.BoletoItemRequest;
import com.a3.boleto.batch.producer.BoletoProducer;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/boleto")
public class BoletoUploadController {

    private final BoletoProducer boletoProducer;

    public BoletoUploadController(BoletoProducer boletoProducer) {
        this.boletoProducer = boletoProducer;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Arquivo CSV não enviado.");
            }

            List<BoletoItemRequest> boletos = new ArrayList<>();

            //Configuração moderna do Commons CSV (sem warnings)
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .build();

            // Lê o CSV e cria a lista de boletos
            try (CSVParser parser = new CSVParser(new InputStreamReader(file.getInputStream()), format)) {
                for (CSVRecord record : parser) {
                    BoletoItemRequest boleto = new BoletoItemRequest();
                    boleto.setCodigoBarras(record.get("codigoBarras"));
                    boleto.setValor(new BigDecimal(record.get("valor")));
                    boleto.setVencimento(LocalDate.parse(record.get("dataVencimento")));
                    boleto.setCnpjBeneficiario(record.get("cnpjEmpresa"));
                    boleto.setNomePagador(record.get("nomePagador"));
                    boleto.setCpfCnpjPagador(record.get("cpfCnpjPagador"));
                    boletos.add(boleto);

                    // Envia para fila (RabbitMQ)
                    boletoProducer.enviarParaFila(boleto);
                }
            }

            //Converte a lista de boletos para JSON
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            String jsonBoletos = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(boletos);


            System.out.println("JSON gerado a partir do CSV:\n" + jsonBoletos);

            // Retorna JSON na resposta (pra você ver no Postman também)
            return ResponseEntity.ok(jsonBoletos);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Erro ao processar CSV: " + e.getMessage());
        }
    }
}
