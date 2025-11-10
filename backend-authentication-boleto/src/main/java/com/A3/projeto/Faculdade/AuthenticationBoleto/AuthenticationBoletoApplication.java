package com.A3.projeto.Faculdade.AuthenticationBoleto;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableRabbit
public class AuthenticationBoletoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationBoletoApplication.class, args);
	}

}
