package com.A3.projeto.Faculdade.AuthenticationBoleto.controller;

import java.security.Principal;
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
    private final com.A3.projeto.Faculdade.AuthenticationBoleto.service.MetricasService metricasService;

    public EmpresaController(EmpresaService service,
            com.A3.projeto.Faculdade.AuthenticationBoleto.service.MetricasService metricasService) {
        this.metricasService = metricasService;
        this.service = service;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<ApiResponse<EmpresaResponse>> salvar(@RequestBody EmpresaRequest request, Principal principal) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Empresa cadastrada com sucesso", service.salvar(request, principal.getName()))
        );
    }

    @GetMapping("/listar")
    public ResponseEntity<ApiResponse<List<EmpresaResponse>>> listarTodas(Principal principal) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Lista de empresas", service.listarDoUsuario(principal.getName()))
        );
    }

    /** JWT: empresas do usuario logado (para obter o id usado em /{id}/metricas). */
    @GetMapping("/minhas")
    public ResponseEntity<ApiResponse<List<EmpresaResponse>>> minhas(Principal principal) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Minhas empresas", service.listarMinhas(principal.getName())));
    }

    /** JWT, so o dono (403 ACESSO_NEGADO). Query opcional de/ate (AAAA-MM-DD, inclusivos). */
    @GetMapping("/{id}/metricas")
    public ResponseEntity<ApiResponse<com.A3.projeto.Faculdade.AuthenticationBoleto.dto.EmpresaMetricasResponse>> metricas(
            @PathVariable Long id, @org.springframework.web.bind.annotation.RequestParam(required = false) String de,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String ate, Principal principal) {
        return ResponseEntity.ok(new ApiResponse<>(true, "M\u00e9tricas da empresa", metricasService.metricas(id, de, ate, principal.getName())));
    }

    @GetMapping("/{cnpj}")
    public ResponseEntity<ApiResponse<EmpresaResponse>> buscarPorCnpj(@PathVariable String cnpj, Principal principal) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Empresa encontrada", service.buscarResponsePorCnpj(cnpj, principal.getName()))
        );
    }
}