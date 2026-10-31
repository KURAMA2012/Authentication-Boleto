package com.A3.projeto.Faculdade.AuthenticationBoleto.common;

import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.ApiException;

/** Normaliza e valida CNPJ: aceita com ou sem mascara e devolve so os 14 digitos. */
public final class Cnpj {

    private Cnpj() {}

    public static String normalizar(String cnpj) {
        String digitos = cnpj == null ? "" : cnpj.replaceAll("\\D", "");
        if (digitos.length() != 14) {
            throw new ApiException(ErrorCode.VALIDACAO_CAMPO,
                    "CNPJ inv\u00e1lido: informe 14 d\u00edgitos.");
        }
        return digitos;
    }
}