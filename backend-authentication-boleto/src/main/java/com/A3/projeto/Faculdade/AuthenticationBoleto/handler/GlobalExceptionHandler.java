package com.A3.projeto.Faculdade.AuthenticationBoleto.handler;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ApiResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ErrorCode;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.ApiException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApi(ApiException ex) {
        return build(ex.getCode(), ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        String message = details.isEmpty() ? "Erro de validação nos campos." : details.get(0);
        return build(ErrorCode.VALIDACAO_CAMPO, message, details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
        return build(ErrorCode.VALIDACAO_CAMPO, "Violação de restrição nos dados enviados.", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(ErrorCode.VALIDACAO_CAMPO, "Corpo da requisição inválido ou mal formatado.", null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        return build(ErrorCode.VALIDACAO_CAMPO,
                "Par\u00e2metro obrigat\u00f3rio ausente: " + ex.getParameterName(),
                List.of(ex.getParameterName() + ": obrigat\u00f3rio"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return build(ErrorCode.REGRA_NEGOCIO, ex.getMessage(), null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Violacao de integridade de dados: {}", ex.getMostSpecificCause().getClass().getSimpleName());
        // Nao expor detalhes do banco (constraint, SQL) ao cliente.
        return build(ErrorCode.REGRA_NEGOCIO, "Registro duplicado ou viola restri\u00e7\u00e3o de integridade.", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        String traceId = UUID.randomUUID().toString();
        log.error("Erro interno traceId={}", traceId, ex);
        // Nao expor detalhes internos ao cliente; o traceId liga a resposta ao log.
        return ResponseEntity.status(ErrorCode.ERRO_INTERNO.getStatus())
                .body(ApiResponse.erro(ErrorCode.ERRO_INTERNO, "Erro interno. Informe o traceId ao suporte.", null, traceId));
    }

    private ResponseEntity<ApiResponse<Void>> build(ErrorCode code, String message, List<String> details) {
        return ResponseEntity.status(code.getStatus()).body(ApiResponse.erro(code, message, details, null));
    }
}