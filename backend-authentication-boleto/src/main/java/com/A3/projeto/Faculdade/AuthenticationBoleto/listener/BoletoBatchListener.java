package com.A3.projeto.Faculdade.AuthenticationBoleto.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.A3.projeto.Faculdade.AuthenticationBoleto.config.RabbitMQConfig;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Boleto;
import com.A3.projeto.Faculdade.AuthenticationBoleto.service.BoletoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component

public class BoletoBatchListener {

	@Autowired
	 private  BoletoService boletoService;
	
		private static final Logger logger = LoggerFactory.getLogger(BoletoBatchListener.class);
	
	    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

	    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
	    public void processarMensagem(String mensagemJson) {
	        try {
	            // Converter JSON recebido para objeto Boleto
	            Boleto boleto = mapper.readValue(mensagemJson, Boleto.class);

	            boletoService.criarBoletoAPartirDoBatch(boleto);

	            logger.info("✅ Boleto recebido e salvo: " + boleto.getCodigoBarras());
	        } catch (Exception e) {
	            logger.info("❌ Erro ao processar boleto: " + e.getMessage());
	        } 
	    }
	    
//	    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
//	    public void processarMensagem(String mensagemJson) {
//	        System.out.println("📩 Mensagem recebida no AuthenticationBoleto: " + mensagemJson);
//	    }
}