package com.A3.projeto.Faculdade.AuthenticationBoleto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
	Optional<Empresa> findByCnpj(String cnpj);
}