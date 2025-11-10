package com.A3.projeto.Faculdade.AuthenticationBoleto.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ApiResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.EmpresaRequest;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.EmpresaResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.service.EmpresaService;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService service;

    public EmpresaController(EmpresaService service) {
        this.service = service;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<ApiResponse<EmpresaResponse>> salvar(@RequestBody EmpresaRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Empresa cadastrada com sucesso", service.salvar(request))
        );
    }

    @GetMapping("/listar")
    public ResponseEntity<ApiResponse<List<EmpresaResponse>>> listarTodas() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Lista de empresas", service.listarTodas())
        );
    }

    @GetMapping("/{cnpj}")
    public ResponseEntity<ApiResponse<EmpresaResponse>> buscarPorCnpj(@PathVariable String cnpj) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Empresa encontrada", service.buscarResponsePorCnpj(cnpj))
        );
    }
}