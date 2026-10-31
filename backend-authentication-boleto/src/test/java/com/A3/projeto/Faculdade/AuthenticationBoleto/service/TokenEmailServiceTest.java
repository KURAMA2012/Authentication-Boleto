package com.A3.projeto.Faculdade.AuthenticationBoleto.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

class TokenEmailServiceTest {

    private JavaMailSender sender;
    private ObjectProvider<JavaMailSender> provider;
    private TokenEmailService service;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        sender = mock(JavaMailSender.class);
        provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(sender);
        service = new TokenEmailService(provider, "no-reply@demo.local", "padrao@demo.local");
    }

    private SimpleMailMessage enviado() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(captor.capture());
        return captor.getValue();
    }

    @Test
    @DisplayName("Envia e-mail com o token e o código do boleto para o destinatário")
    void enviaEmailComToken() {
        service.enviarToken("pagador@demo.local", "AUTH-1", "123456");

        SimpleMailMessage msg = enviado();
        assertThat(msg.getTo()).containsExactly("pagador@demo.local");
        assertThat(msg.getFrom()).isEqualTo("no-reply@demo.local");
        assertThat(msg.getText()).contains("123456").contains("AUTH-1");
    }

    @Test
    @DisplayName("Sem destinatário, usa o e-mail padrão de demonstração")
    void semDestinatarioUsaPadrao() {
        service.enviarToken("  ", "AUTH-1", "123456");

        assertThat(enviado().getTo()).containsExactly("padrao@demo.local");
    }

    @Test
    @DisplayName("Falha no SMTP não propaga exceção")
    void falhaNoEnvioNaoQuebraFluxo() {
        doThrow(new MailSendException("SMTP fora")).when(sender).send(any(SimpleMailMessage.class));

        assertThatCode(() -> service.enviarToken("a@demo.local", "AUTH-1", "123456")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Sem JavaMailSender configurado, não tenta enviar")
    void semMailSenderNaoEnvia() {
        when(provider.getIfAvailable()).thenReturn(null);

        assertThatCode(() -> service.enviarToken("a@demo.local", "AUTH-1", "123456")).doesNotThrowAnyException();
    }
}