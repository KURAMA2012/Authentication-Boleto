package com.A3.projeto.Faculdade.AuthenticationBoleto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ApiResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.LoginResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.UsuarioCadastroDTO;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Usuario;
import com.A3.projeto.Faculdade.AuthenticationBoleto.security.JwtService;
import com.A3.projeto.Faculdade.AuthenticationBoleto.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class UsuarioController {

    private final UsuarioService service;
    private final JwtService jwtService;

    public UsuarioController(UsuarioService service, JwtService jwtService) {
        this.service = service;
        this.jwtService = jwtService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<ApiResponse<LoginResponse>> cadastrar(@Valid @RequestBody UsuarioCadastroDTO dto) {
        Usuario usuario = service.cadastrar(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Usuário cadastrado com sucesso!", semToken(usuario)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestParam String email, @RequestParam String senha) {
        Usuario usuario = service.login(email, senha);
        String token = jwtService.gerarToken(usuario.getEmail());
        LoginResponse body = new LoginResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(),
                token, "Bearer", jwtService.getExpirationMs());
        return ResponseEntity.ok(new ApiResponse<>(true, "Login realizado com sucesso!", body));
    }

    private LoginResponse semToken(Usuario u) {
        return new LoginResponse(u.getId(), u.getNome(), u.getEmail(), null, null, 0);
    }
}