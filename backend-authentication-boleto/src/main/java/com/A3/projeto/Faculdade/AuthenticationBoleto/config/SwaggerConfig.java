package com.A3.projeto.Faculdade.AuthenticationBoleto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI authenticationBoletoAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🔐 Authentication Boleto Service API")
                        .version("1.0.0")
                        .description("Serviço responsável por autenticar, validar tokens e processar boletos recebidos do RabbitMQ.")
                        .contact(new Contact()
                                .name("Equipe A3 - Auth")
                                .email("auth@a3.com.br")));
    }
}