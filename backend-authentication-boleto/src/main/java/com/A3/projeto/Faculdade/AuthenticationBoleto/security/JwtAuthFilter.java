package com.A3.projeto.Faculdade.AuthenticationBoleto.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.ApiException;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Le "Authorization: Bearer <jwt>" e autentica a requisicao. Sem header, segue anonima. */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String ATTR_ERRO = "jwt.erro";

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) { this.jwtService = jwtService; }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            try {
                Claims claims = jwtService.validarClaims(header.substring(7));
                String role = claims.get(JwtService.CLAIM_ROLE, String.class);
                if (role == null || role.isBlank()) role = "EMPRESA";
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                        claims.getSubject(), null, List.of(new SimpleGrantedAuthority("ROLE_" + role))));
            } catch (ApiException e) {
                // Guardado para o entry point responder com o code certo (TOKEN_EXPIRADO x TOKEN_INVALIDO).
                request.setAttribute(ATTR_ERRO, e.getCode());
            }
        }
        chain.doFilter(request, response);
    }
}