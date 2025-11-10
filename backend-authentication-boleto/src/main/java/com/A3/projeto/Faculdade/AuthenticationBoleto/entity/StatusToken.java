package com.A3.projeto.Faculdade.AuthenticationBoleto.entity;

public enum StatusToken {

    ATIVO("Token ativo e válido"),

    EXPIRADO("Token expirado"),

    UTILIZADO("Token já utilizado"),

    CANCELADO("Token cancelado");
	
    private final String descricao;

    StatusToken(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusToken fromString(String value) {
        for (StatusToken status : StatusToken.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("StatusToken inválido: " + value);
    }

    @Override
    public String toString() {
        return this.name() + " (" + descricao + ")";
    }
}