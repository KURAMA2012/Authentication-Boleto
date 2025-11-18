package com.A3.projeto.Faculdade.AuthenticationBoleto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ApiResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.UsuarioCadastroDTO;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Usuario;
import com.A3.projeto.Faculdade.AuthenticationBoleto.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class UsuarioController {

	@Autowired
    private UsuarioService service;


    @PostMapping("/cadastrar")
    public ResponseEntity<ApiResponse<Usuario>> cadastrar(@Valid @RequestBody UsuarioCadastroDTO dto) {
        Usuario usuario = service.cadastrar(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Usuário cadastrado com sucesso!", usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Usuario>> login(@RequestParam String email, @RequestParam String senha) {

        Usuario usuario = service.login(email, senha);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login realizado com sucesso!", usuario));
    }
}
