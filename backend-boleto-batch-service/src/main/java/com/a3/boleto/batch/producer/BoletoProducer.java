package com.a3.boleto.batch.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.a3.boleto.batch.dto.BoletoItemRequest;

@Component
public class BoletoProducer {

    private final RabbitTemplate rabbitTemplate;
    private static final String QUEUE_NAME = "boleto.criar.queue";

    public BoletoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarParaFila(BoletoItemRequest boleto) {
        rabbitTemplate.convertAndSend(QUEUE_NAME, boleto);
        System.out.println("Enviado para fila: " + boleto.getCodigoBarras());
    }
}
