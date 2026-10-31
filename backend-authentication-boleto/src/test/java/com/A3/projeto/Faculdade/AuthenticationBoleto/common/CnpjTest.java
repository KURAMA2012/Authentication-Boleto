package com.A3.projeto.Faculdade.AuthenticationBoleto.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.ApiException;

class CnpjTest {

    @Test
    void removeMascara() {
        assertThat(Cnpj.normalizar("61.099.834/0001-90")).isEqualTo("61099834000190");
    }

    @Test
    void aceitaSoDigitos() {
        assertThat(Cnpj.normalizar("61099834000190")).isEqualTo("61099834000190");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"123", "610998340001901", "abc"})
    void rejeitaInvalidoComValidacaoCampo(String cnpj) {
        assertThatThrownBy(() -> Cnpj.normalizar(cnpj))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getCode())
                .isEqualTo(ErrorCode.VALIDACAO_CAMPO);
    }
}