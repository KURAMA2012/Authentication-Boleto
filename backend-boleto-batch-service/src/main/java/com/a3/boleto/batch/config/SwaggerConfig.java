package com.a3.boleto.batch.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI boletoBatchAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("📦 Boleto Batch Service API")
                        .version("1.0.0")
                        .description("Serviço responsável por importar CSVs de boletos e enviar para o RabbitMQ.")
                        .contact(new Contact()
                                .name("Equipe A3 - Batch")
                                .email("batch@a3.com.br")));
    }
}