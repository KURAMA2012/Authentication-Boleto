package com.A3.projeto.Faculdade.AuthenticationBoleto.entity;

public enum StatusBoleto {
    GERADO("Boleto gerado"),
    AUTENTICADO("Boleto Autenticado "),
    EM_PROCESSAMENTO("Em processamento"),
    PAGO("Pago com sucesso"),
    VENCIDO("Vencido"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusBoleto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}