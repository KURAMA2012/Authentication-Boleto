package com.A3.projeto.Faculdade.AuthenticationBoleto.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoLogResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Usuario;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.BoletoLogRepository;
import com.A3.projeto.Faculdade.AuthenticationBoleto.service.EmpresaService;

@RestController
@RequestMapping("/api/logs")
public class BoletoLogController {

    private final BoletoLogRepository boletoLogRepository;
    private final EmpresaService empresaService;

    public BoletoLogController(BoletoLogRepository boletoLogRepository, EmpresaService empresaService) {
        this.boletoLogRepository = boletoLogRepository;
        this.empresaService = empresaService;
    }

    /** JWT: EMPRESA ve so os logs das empresas dela (boleto ou cnpjInformado); ADMIN ve todos. */
    @GetMapping
    @Transactional(readOnly = true)
    public List<BoletoLogResponse> listarTodos(Principal principal) {
        Usuario u = empresaService.usuarioLogado(principal.getName());
        var logs = u.isAdmin() ? boletoLogRepository.findAll() : boletoLogRepository.findDoDono(u.getId());
        return logs.stream().map(BoletoLogResponse::de).toList();
    }
}