package com.A3.projeto.Faculdade.AuthenticationBoleto.security;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ApiResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Rate limit simples em memoria (janela fixa) por IP + rota, so nas rotas publicas do pagador.
 * Excedeu: 429 LIMITE_TENTATIVAS no contrato de erro. Usa o IP da conexao (nao o X-Forwarded-For,
 * que o cliente pode forjar). Em memoria: vale por instancia e zera ao reiniciar.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    static final Set<String> ROTAS = Set.of("/api/public/boleto/validar", "/api/public/boleto/enviar-token", "/api/public/boleto/confirmar",
            "/api/boleto/validar", "/api/boleto/enviar-token", "/api/boleto/confirmar");

    private final int maxRequisicoes;
    private final long janelaMs;
    private final LongSupplier relogio;
    private final Map<String, long[]> janelas = new ConcurrentHashMap<>(); // chave -> {inicioJanela, contagem}
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
			.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // timestamp ISO, igual ao handler

    @Autowired
    public RateLimitFilter(@Value("${app.rate-limit.max-requests:10}") int maxRequisicoes,
                           @Value("${app.rate-limit.window-seconds:60}") long janelaSegundos) {
        this(maxRequisicoes, janelaSegundos, System::currentTimeMillis);
    }

    RateLimitFilter(int maxRequisicoes, long janelaSegundos, LongSupplier relogio) {
        this.maxRequisicoes = maxRequisicoes;
        this.janelaMs = janelaSegundos * 1000;
        this.relogio = relogio;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equalsIgnoreCase(request.getMethod()) || !ROTAS.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (!permitir(request.getRemoteAddr() + "|" + request.getRequestURI())) {
            response.setStatus(ErrorCode.LIMITE_TENTATIVAS.getStatus().value());
            response.setHeader("Retry-After", String.valueOf(janelaMs / 1000));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            mapper.writeValue(response.getWriter(), ApiResponse.erro(ErrorCode.LIMITE_TENTATIVAS,
                    "Muitas requisi\u00e7\u00f5es. Tente novamente em instantes.", null, null));
            return;
        }
        chain.doFilter(request, response);
    }

    boolean permitir(String chave) {
        long agora = relogio.getAsLong();
        if (janelas.size() > 10_000) {
            janelas.values().removeIf(j -> agora - j[0] >= janelaMs); // limpeza simples
        }
        long[] j = janelas.compute(chave, (k, atual) -> {
            if (atual == null || agora - atual[0] >= janelaMs) return new long[] {agora, 1};
            atual[1]++;
            return atual;
        });
        return j[1] <= maxRequisicoes;
    }
}