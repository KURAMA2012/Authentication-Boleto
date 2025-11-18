package com.A3.projeto.Faculdade.AuthenticationBoleto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
