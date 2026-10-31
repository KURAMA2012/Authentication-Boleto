package com.A3.projeto.Faculdade.AuthenticationBoleto.dto;

/** Resposta de login: dados publicos do usuario + JWT. Nunca expoe a senha. */
public record LoginResponse(Long id, String nome, String email, String token, String tipo, long expiraEmMs) {
}