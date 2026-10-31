package com.A3.projeto.Faculdade.AuthenticationBoleto.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envia o token de verificação por e-mail.
 * Em dev/demo o SMTP aponta para o Mailpit (nenhum e-mail sai de verdade).
 * Falha no envio só é registrada em log: o fluxo do token não é interrompido.
 */
@Service
public class TokenEmailService {

    private static final Logger log = LoggerFactory.getLogger(TokenEmailService.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String remetente;
    private final String destinatarioPadrao;

    public TokenEmailService(ObjectProvider<JavaMailSender> mailSender,
                             @Value("${app.mail.from:no-reply@authboleto.local}") String remetente,
                             @Value("${app.mail.default-to:demo@authboleto.local}") String destinatarioPadrao) {
        this.mailSender = mailSender;
        this.remetente = remetente;
        this.destinatarioPadrao = destinatarioPadrao;
    }

    public void enviarToken(String destinatario, String codigoAutenticacao, String token) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            log.warn("JavaMailSender não configurado; token do boleto {} não enviado por e-mail.", codigoAutenticacao);
            return;
        }
        String para = (destinatario == null || destinatario.isBlank()) ? destinatarioPadrao : destinatario;
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(remetente);
            msg.setTo(para);
            msg.setSubject("Seu código de verificação do boleto");
            msg.setText("Olá!\n\nSeu código de verificação para o boleto " + codigoAutenticacao + " é: " + token
                    + "\n\nEle expira em 5 minutos.\n\nSe você não solicitou, ignore este e-mail.");
            sender.send(msg);
            log.info("Token do boleto {} enviado por e-mail para {}.", codigoAutenticacao, para);
        } catch (Exception e) {
            log.error("Falha ao enviar token do boleto {} por e-mail: {}", codigoAutenticacao, e.getMessage());
        }
    }
}