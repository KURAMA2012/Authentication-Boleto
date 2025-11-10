package com.A3.projeto.Faculdade.AuthenticationBoleto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Boleto;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.TokenVerificacao;

@Repository
public interface TokenVerificacaoRepository extends JpaRepository<TokenVerificacao, Long> {
    Optional<TokenVerificacao> findByBoletoAndUtilizadoFalse(Boleto boleto);
}