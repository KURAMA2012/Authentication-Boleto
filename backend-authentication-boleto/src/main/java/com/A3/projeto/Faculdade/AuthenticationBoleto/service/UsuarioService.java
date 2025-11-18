package com.A3.projeto.Faculdade.AuthenticationBoleto.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.UsuarioCadastroDTO;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Usuario;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario cadastrar(UsuarioCadastroDTO dto) {

        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        
        // Criptografa a senha  🔐
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        
        usuario.setTelefone(dto.getTelefone());
        usuario.setCpf(dto.getCpf());

        return repository.save(usuario);
    }

    public Usuario login(String email, String senha) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Senha incorreta");
        }

        return usuario;
    }
}
