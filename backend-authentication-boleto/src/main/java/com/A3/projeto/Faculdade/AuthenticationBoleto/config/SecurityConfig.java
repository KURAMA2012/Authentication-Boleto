package com.A3.projeto.Faculdade.AuthenticationBoleto.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.A3.projeto.Faculdade.AuthenticationBoleto.security.JsonAuthEntryPoint;
import com.A3.projeto.Faculdade.AuthenticationBoleto.security.JwtAuthFilter;

@Configuration
public class SecurityConfig {

    /** Rotas publicas: login/cadastro, documentacao e preflight CORS. O resto exige JWT. */
    static final String[] ROTAS_PUBLICAS = {
            "/api/auth/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/error"
    };

    /** Fluxo publico do pagador (sem login). Protegido por rate limit por IP (RateLimitFilter). */
    static final String[] ROTAS_PUBLICAS_POST = {
            "/api/public/boleto/validar", "/api/public/boleto/enviar-token", "/api/public/boleto/confirmar",
            "/api/boleto/validar", "/api/boleto/enviar-token", "/api/boleto/confirmar"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${security.cors.allowed-origins:http://localhost:8100,http://localhost:4200,http://localhost}") List<String> origins) {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(origins);
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtFilter, JsonAuthEntryPoint entryPoint) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // API stateless com Bearer token: sem cookie de sessao, sem CSRF.
            .cors(cors -> {})
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(ROTAS_PUBLICAS).permitAll()
                .requestMatchers(HttpMethod.POST, ROTAS_PUBLICAS_POST).permitAll()
                .anyRequest().authenticated())
            .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint).accessDeniedHandler(entryPoint))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(h -> h.disable())
            .formLogin(f -> f.disable());
        return http.build();
    }
}