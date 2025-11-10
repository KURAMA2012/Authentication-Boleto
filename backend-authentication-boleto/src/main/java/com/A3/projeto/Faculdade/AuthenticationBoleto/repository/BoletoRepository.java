package com.A3.projeto.Faculdade.AuthenticationBoleto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Boleto;

@Repository
public interface BoletoRepository extends JpaRepository<Boleto, Long> {
    Boleto findByCodigoAutenticacao(String codigo);
    
    @Query("SELECT b FROM Boleto b WHERE b.codigoBarras = :codigoBarras")
    Boleto buscarPorCodigoBarras(@Param("codigoBarras") String codigoBarras);
    
    @Query("SELECT b FROM Boleto b WHERE b.codigoAutenticacao = :codigoAutenticacao")
    Boleto buscarPorCodigoAutenticacao(@Param("codigoAutenticacao") String codigoAutenticacao);
    
    
}