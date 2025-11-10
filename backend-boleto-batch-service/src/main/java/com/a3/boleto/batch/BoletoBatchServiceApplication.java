package com.a3.boleto.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.a3.boleto.batch.client")
public class BoletoBatchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoletoBatchServiceApplication.class, args);
	}

}
