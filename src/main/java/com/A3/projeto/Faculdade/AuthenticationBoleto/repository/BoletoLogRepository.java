package com.A3.projeto.Faculdade.AuthenticationBoleto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.BoletoLog;

public interface BoletoLogRepository extends JpaRepository<BoletoLog, Long> {
}