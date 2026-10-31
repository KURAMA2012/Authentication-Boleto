package com.A3.projeto.Faculdade.AuthenticationBoleto.common;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Envelope unico de resposta da API.
 * Sucesso: success=true, data preenchido.
 * Erro: success=false, code estavel (ver ErrorCode), message legivel e details opcionais.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final String code;
    private final String message;
    private final List<String> details;
    private final String traceId;
    private final T data;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public ApiResponse(boolean success, String message, T data) {
        this(success, null, message, null, null, data);
    }

    public ApiResponse(boolean success, String message) {
        this(success, null, message, null, null, null);
    }

    private ApiResponse(boolean success, String code, String message, List<String> details, String traceId, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.details = details;
        this.traceId = traceId;
        this.data = data;
    }

    public static <T> ApiResponse<T> erro(ErrorCode code, String message, List<String> details, String traceId) {
        return new ApiResponse<>(false, code.name(), message, details == null || details.isEmpty() ? null : details, traceId, null);
    }

    public boolean isSuccess() { return success; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public List<String> getDetails() { return details; }
    public String getTraceId() { return traceId; }
    public T getData() { return data; }
    public LocalDateTime getTimestamp() { return timestamp; }
}