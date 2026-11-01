package com.A3.projeto.Faculdade.AuthenticationBoleto.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ErrorCode;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.ApiException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/** Emite e valida JWT HS256. Segredo e expiracao vem de JWT_SECRET e JWT_EXPIRATION_MS. */
@Service
public class JwtService {

    public static final String CLAIM_ROLE = "role";

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(@Value("${security.jwt.secret}") String secret,
                      @Value("${security.jwt.expiration-ms:3600000}") long expirationMs) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET precisa ter pelo menos 32 caracteres.");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String gerarToken(String email) {
        return gerarToken(email, "EMPRESA");
    }

    /** Gera o JWT com o claim "role" (EMPRESA ou ADMIN). */
    public String gerarToken(String email, String role) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_ROLE, role)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plusMillis(expirationMs)))
                .signWith(key)
                .compact();
    }

    public long getExpirationMs() { return expirationMs; }

    /** Retorna o email (subject) ou lanca ApiException com TOKEN_EXPIRADO / TOKEN_INVALIDO. */
    public String validar(String token) {
        return validarClaims(token).getSubject();
    }

    /** Valida e devolve os claims (subject = email, role). Mesmos erros de validar(). */
    public Claims validarClaims(String token) {
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            throw new ApiException(ErrorCode.TOKEN_EXPIRADO, "Sessão expirada. Faça login novamente.");
        } catch (JwtException | IllegalArgumentException e) {
            throw new ApiException(ErrorCode.TOKEN_INVALIDO, "Token inválido.");
        }
    }
}