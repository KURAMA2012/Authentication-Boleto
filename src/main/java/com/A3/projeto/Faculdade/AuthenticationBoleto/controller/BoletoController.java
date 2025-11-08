package com.A3.projeto.Faculdade.AuthenticationBoleto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ApiResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoConfirmacaoRequest;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoRequest;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoValidacaoRequest;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoValidacaoResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Boleto;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.StatusBoleto;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.EmpresaNaoEncontradaException;
import com.A3.projeto.Faculdade.AuthenticationBoleto.service.BoletoLogService;
import com.A3.projeto.Faculdade.AuthenticationBoleto.service.BoletoService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/boleto")
public class BoletoController {
	
	@Autowired
	private BoletoLogService boletoLogService;

	@Autowired
    private BoletoService boletoService;

    public BoletoController(BoletoService boletoService) {
        this.boletoService = boletoService;
    }
    
    
    @PostMapping("/criar")
    public ResponseEntity<ApiResponse<BoletoResponse>> criar(@RequestBody BoletoRequest request) {
        try {
            Boleto boleto = boletoService.criarBoleto(request);

            return ResponseEntity.ok(
                new ApiResponse<>(true, "Boleto criado com sucesso", new BoletoResponse(boleto))
            );

        } catch (EmpresaNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, "Não é possível criar boleto: empresa não encontrada", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Erro ao criar boleto: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/buscar/{codigoBarras}")
    public ResponseEntity<ApiResponse<BoletoResponse>> buscarPorCodigoBarras(@PathVariable String codigoBarras) {
        Boleto boleto = boletoService.buscarPorCodigoBarras(codigoBarras);

        if (boleto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, "Boleto não encontrado", null));
        }

        return ResponseEntity.ok(
            new ApiResponse<>(true, "Boleto encontrado", new BoletoResponse(boleto))
        );
    }




//    @PostMapping("/validar/{codigoAutenticacao}")
//    public ResponseEntity<ApiResponse<BoletoValidacaoResponse>> validarBoleto(@RequestBody BoletoValidacaoRequest request, HttpServletRequest servletRequest) {
//        boolean valido = boletoService.validarCodigoAutenticacao(request.getCodigoAutenticacao());
//        if (!valido) {
//            throw new BoletoNaoEncontradoException("Boleto não encontrado ou código inválido.");
//        }
//        
//        // captura IP do cliente (considera proxy)
//        String ip = extractClientIp(servletRequest); 
//        
//        // simular a validação real do boleto
//        valido = true;
//        String mensagem = "Boleto autenticado com sucesso.";
//        
//        boletoLogService.registrarLog(
//                request.getCodigoAutenticacao(),
//                ip,
//                valido,
//                mensagem,
//                null // se tiver o boleto real, passa aqui
//            );
//        return ResponseEntity.ok(
//                new ApiResponse<>(
//                    true,
//                    "Boleto autenticado com sucesso.",
//                    new BoletoValidacaoResponse(true, request.getCodigoAutenticacao())
//                )
//            );
//        }
    
    
    @PostMapping("/validar")
    public ResponseEntity<ApiResponse<BoletoValidacaoResponse>> validarBoleto(
            @RequestBody BoletoValidacaoRequest request,
            HttpServletRequest servletRequest) {

        // 1. Busca o boleto pelo código de autenticação
        Boleto boleto = boletoService.buscarPorCodigoAutenticacao(request.getCodigoAutenticacao());
        // 2. Captura o IP do cliente
        String ip = extractClientIp(servletRequest);

        // 3. Atualiza status do boleto
        boleto.setAutenticado(true);
        boleto.setStatus(StatusBoleto.AUTENTICADO);
        boletoService.atualizarBoleto(boleto);

        // 4. Registra log da validação
        boletoLogService.registrarLog(
                boleto.getCodigoAutenticacao(),
                ip,
                true,
                "Boleto autenticado com sucesso.",
                boleto
        );

        // 5. Retorna resposta
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Boleto autenticado com sucesso.",
                        new BoletoValidacaoResponse(true, boleto.getCodigoAutenticacao())
                )
        );
    }

    
    
    // 🟢 Enviar token
    @PostMapping("/enviar-token")
    public ResponseEntity<ApiResponse<String>> enviarToken(@RequestBody BoletoValidacaoRequest request) {
        String token = boletoService.gerarOuRecuperarToken(request.getCodigoAutenticacao());
        
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Token de verificação enviado com sucesso (simulado). Token: " + token)
        );
    }


    
    
    // 🟣 Confirmar token
    @PostMapping("/confirmar")
    public ResponseEntity<ApiResponse<String>> confirmarToken(@RequestBody BoletoConfirmacaoRequest request) {
        boolean confirmado = boletoService.confirmarToken(request.getCodigoAutenticacao(), request.getTokenVerificacao());

        if (!confirmado) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Token inválido ou boleto não encontrado."));
        }

        return ResponseEntity.ok(
            new ApiResponse<>(true, "Boleto confirmado com sucesso! Pagamento pode prosseguir.")
        );
    }
    
    private String extractClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isBlank()) {
            // X-Forwarded-For pode conter vários IPs: cliente, proxies...
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
