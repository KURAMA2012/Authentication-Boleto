package com.A3.projeto.Faculdade.AuthenticationBoleto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.BoletoLog;

@Repository
public interface BoletoLogRepository extends JpaRepository<BoletoLog, Long> {

    /** Logs do dono: do boleto de uma empresa dele, ou com cnpjInformado = CNPJ de uma empresa dele. */
    @Query("SELECT l FROM BoletoLog l LEFT JOIN l.boleto b LEFT JOIN b.empresa e "
         + "WHERE e.dono.id = :donoId "
         + "OR l.cnpjInformado IN (SELECT e2.cnpj FROM Empresa e2 WHERE e2.dono.id = :donoId) "
         + "ORDER BY l.dataConsulta DESC")
    List<BoletoLog> findDoDono(@Param("donoId") Long donoId);

    /** Logs de uma empresa (pelo boleto ou pelo cnpjInformado), para as metricas. */
    @Query("SELECT l FROM BoletoLog l LEFT JOIN l.boleto b "
         + "WHERE b.empresa.id = :empresaId OR l.cnpjInformado = :cnpj")
    List<BoletoLog> findDaEmpresa(@Param("empresaId") Long empresaId, @Param("cnpj") String cnpj);
}