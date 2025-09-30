package auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import auth.dto.LoginRequestDTO;
import auth.dto.LoginResponseDTO;
import auth.dto.RegisterRequestDTO;
import auth.entity.Usuario;
import auth.service.AuthService;
import auth.service.UsuarioService;

public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private UsuarioService usuarioService;

	@PostMapping("/login")
	public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
		return authService.autenticar(request);
	}

	@PostMapping("/register")
	public Usuario register(@RequestBody RegisterRequestDTO request) {
		return usuarioService.criarUsuario(request.getNome(), request.getEmail(), request.getSenha());
	}
}
