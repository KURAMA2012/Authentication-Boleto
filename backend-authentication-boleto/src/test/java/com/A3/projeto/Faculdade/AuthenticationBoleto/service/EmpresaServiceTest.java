package com.A3.projeto.Faculdade.AuthenticationBoleto.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ErrorCode;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.EmpresaRequest;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Empresa;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.ApiException;
import com.A3.projeto.Faculdade.AuthenticationBoleto.mapper.EmpresaMapper;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.EmpresaRepository;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    private static final String CNPJ_MASCARA = "61.099.834/0001-90";
    private static final String CNPJ_DIGITOS = "61099834000190";

    @Mock
    private EmpresaRepository repository;

    @Mock
    private EmpresaMapper empresaMapper;

    @InjectMocks
    private EmpresaService service;

    @Test
    void salvar_cnpjComMascara_persisteSomenteDigitos() {
        EmpresaRequest request = new EmpresaRequest();
        request.setNome("Empresa Demo");
        request.setCnpj(CNPJ_MASCARA);
        when(empresaMapper.toEntity(any(EmpresaRequest.class))).thenAnswer(inv -> {
            EmpresaRequest r = inv.getArgument(0);
            Empresa e = new Empresa();
            e.setNome(r.getNome());
            e.setCnpj(r.getCnpj());
            return e;
        });
        when(repository.save(any(Empresa.class))).thenAnswer(inv -> inv.getArgument(0));

        service.salvar(request);

        ArgumentCaptor<Empresa> captor = ArgumentCaptor.forClass(Empresa.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getCnpj()).isEqualTo(CNPJ_DIGITOS);
    }

    @Test
    void salvar_cnpjJaCadastrado_lancaEmpresaDuplicadaESemPersistir() {
        EmpresaRequest request = new EmpresaRequest();
        request.setCnpj(CNPJ_MASCARA);
        when(repository.existsByCnpj(CNPJ_DIGITOS)).thenReturn(true);

        assertThatThrownBy(() -> service.salvar(request))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo(ErrorCode.EMPRESA_DUPLICADA);
        verify(repository, never()).save(any());
        verifyNoInteractions(empresaMapper);
    }

    @Test
    void salvar_cnpjInvalido_lancaValidacaoESemPersistir() {
        EmpresaRequest request = new EmpresaRequest();
        request.setCnpj("123");

        assertThatThrownBy(() -> service.salvar(request)).isInstanceOf(ApiException.class);
        verifyNoInteractions(empresaMapper, repository);
    }

    @Test
    void buscarResponsePorCnpj_cnpjComMascara_consultaPorDigitos() {
        Empresa empresa = new Empresa();
        empresa.setCnpj(CNPJ_DIGITOS);
        when(repository.findByCnpj(CNPJ_DIGITOS)).thenReturn(Optional.of(empresa));

        assertThat(service.buscarResponsePorCnpj(CNPJ_MASCARA)).isNotNull();
        verify(repository).findByCnpj(CNPJ_DIGITOS);
    }

    @Test
    void buscarResponsePorCnpj_cnpjInvalido_naoConsultaBanco() {
        assertThatThrownBy(() -> service.buscarResponsePorCnpj("abc")).isInstanceOf(ApiException.class);
        verifyNoInteractions(repository);
    }

    @Test
    void listarTodas_mapeiaCadaCampoNoLugarCerto() {
        Empresa e = new Empresa();
        e.setId(1L); e.setNome("Empresa Demo"); e.setCnpj(CNPJ_DIGITOS); e.setEmailContato("contato@demo.local");
        e.setTelefone("11999990000"); e.setNomeFantasia("Demo"); e.setCep("01001000"); e.setLogradouro("Rua Exemplo");
        e.setNumero("100"); e.setBairro("Centro"); e.setMunicipio("Sao Paulo"); e.setUf("SP");
        when(repository.findAll()).thenReturn(java.util.List.of(e));

        var r = service.listarTodas().get(0);

        assertThat(r.getTelefone()).isEqualTo("11999990000");
        assertThat(r.getFantasia()).isEqualTo("Demo");
        assertThat(r.getCep()).isEqualTo("01001000");
        assertThat(r.getLogradouro()).isEqualTo("Rua Exemplo");
        assertThat(r.getNumero()).isEqualTo("100");
        assertThat(r.getBairro()).isEqualTo("Centro");
        assertThat(r.getMunicipio()).isEqualTo("Sao Paulo");
        assertThat(r.getUf()).isEqualTo("SP");
    }
}