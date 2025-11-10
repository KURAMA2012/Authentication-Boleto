package com.a3.boleto.batch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a3.boleto.batch.dto.BoletoLoteRequest;
import com.a3.boleto.batch.dto.BoletoLoteResponse;
import com.a3.boleto.batch.service.BoletoBatchService;

@RestController
@RequestMapping("/api/boleto/lote")
@CrossOrigin(origins = "http://localhost:8100")
public class BoletoBatchController {

    private final BoletoBatchService service;

    public BoletoBatchController(BoletoBatchService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BoletoLoteResponse> processarLote(@RequestBody BoletoLoteRequest request) {
        return ResponseEntity.ok(service.processarLote(request));
    }
}