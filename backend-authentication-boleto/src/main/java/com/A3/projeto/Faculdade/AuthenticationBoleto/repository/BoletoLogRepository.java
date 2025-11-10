package com.A3.projeto.Faculdade.AuthenticationBoleto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.BoletoLog;

@Repository
public interface BoletoLogRepository extends JpaRepository<BoletoLog, Long> {
}