package com.A3.projeto.Faculdade.AuthenticationBoleto.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "boleto.criar.queue";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true); // durable = true
    }
}
