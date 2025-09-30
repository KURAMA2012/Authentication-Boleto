package auth.service;

import org.springframework.stereotype.Service;

import auth.dto.LoginRequestDTO;
import auth.dto.LoginResponseDTO;
import auth.entity.Usuario;
import auth.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public LoginResponseDTO autenticar(LoginRequestDTO login) {
        Usuario usuario = usuarioRepository.findByEmail(login.getEmail())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!usuario.getSenha().equals(login.getSenha())) {
            throw new RuntimeException("Senha incorreta");
        }

        // faker JWT implementar depois
        String tokenFake = "fake-token-" + usuario.getId();

        return new LoginResponseDTO(tokenFake, usuario.getNome());
    }
}