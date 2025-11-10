package com.a3.boleto.batch.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.a3.boleto.batch.dto.BoletoItemRequest;

@FeignClient(
	    name = "boleto-service",
	    url = "${boleto.service.url}"
	)
public interface BoletoApiClient {

    @PostMapping("/criar")
    Object criarBoleto(@RequestBody BoletoItemRequest boleto);
}