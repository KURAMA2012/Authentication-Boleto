package com.A3.projeto.Faculdade.AuthenticationBoleto.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/consulta")
public class ConsultaController {

    private static final String RECEITA_URL = "https://receitaws.com.br/v1/cnpj/";
    private static final String VIACEP_URL = "https://viacep.com.br/ws/";

    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init() {
        System.out.println("✅ ConsultaController carregado com sucesso!");
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<Map<String, Object>> consultarCnpj(@PathVariable String cnpj) {
        try {
            String url = RECEITA_URL + cnpj.replaceAll("\\D", "");
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);

            if (response == null || (response.get("status") != null && !"OK".equals(response.get("status")))) {
                return ResponseEntity.badRequest().body(Map.of("erro", "CNPJ não encontrado ou inválido."));
            }

            Map<String, Object> dados = new HashMap<>();
            dados.put("cnpj", response.get("cnpj"));
            dados.put("nome", response.get("nome"));
            dados.put("fantasia", response.get("fantasia"));
            dados.put("email", response.get("email"));
            dados.put("telefone", response.get("telefone"));
            dados.put("logradouro", response.get("logradouro"));
            dados.put("numero", response.get("numero"));
            dados.put("bairro", response.get("bairro"));
            dados.put("municipio", response.get("municipio"));
            dados.put("uf", response.get("uf"));
            dados.put("cep", response.get("cep"));

            return ResponseEntity.ok(dados);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("erro", "Erro ao consultar CNPJ: " + e.getMessage()));
        }
    }

    @GetMapping("/cep/{cep}")
    public ResponseEntity<Map<String, Object>> consultarCep(@PathVariable String cep) {
        try {
            String url = VIACEP_URL + cep.replaceAll("\\D", "") + "/json/";
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);

            if (response == null || response.get("erro") != null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "CEP inválido ou não encontrado."));
            }

            Map<String, Object> dados = new HashMap<>();
            dados.put("cep", response.get("cep"));
            dados.put("logradouro", response.get("logradouro"));
            dados.put("bairro", response.get("bairro"));
            dados.put("localidade", response.get("localidade"));
            dados.put("uf", response.get("uf"));
            dados.put("complemento", response.get("complemento"));

            return ResponseEntity.ok(dados);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("erro", "Erro ao consultar CEP: " + e.getMessage()));
        }
    }
}
