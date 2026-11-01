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
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.UsuarioRepository;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Role;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Usuario;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    private static final String CNPJ_MASCARA = "61.099.834/0001-90";
    private static final String CNPJ_DIGITOS = "61099834000190";

    @Mock
    private EmpresaRepository repository;

    @Mock
    private EmpresaMapper empresaMapper;

    @Mock
    private UsuarioRepository usuarioRepository;

    private static final String EMAIL = "dono@demo.local";
    private static final String OUTRO = "outro@demo.local";

    private Usuario usuario(long id, String email, Role role) {
        Usuario u = new Usuario("Usuario Demo", email, "x", null, null);
        u.setId(id);
        u.setRole(role);
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(u));
        return u;
    }

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

        Usuario dono = usuario(1L, EMAIL, Role.EMPRESA);
        service.salvar(request, EMAIL);

        ArgumentCaptor<Empresa> captor = ArgumentCaptor.forClass(Empresa.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getCnpj()).isEqualTo(CNPJ_DIGITOS);
        assertThat(captor.getValue().getDono()).isSameAs(dono);
    }

    @Test
    void salvar_cnpjJaCadastrado_lancaEmpresaDuplicadaESemPersistir() {
        EmpresaRequest request = new EmpresaRequest();
        request.setCnpj(CNPJ_MASCARA);
        when(repository.existsByCnpj(CNPJ_DIGITOS)).thenReturn(true);

        assertThatThrownBy(() -> service.salvar(request, EMAIL))
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

        assertThatThrownBy(() -> service.salvar(request, EMAIL)).isInstanceOf(ApiException.class);
        verifyNoInteractions(empresaMapper, repository);
    }

    @Test
    void buscarResponsePorCnpj_cnpjComMascara_consultaPorDigitos() {
        Empresa empresa = new Empresa();
        empresa.setCnpj(CNPJ_DIGITOS);
        empresa.setDono(usuario(1L, EMAIL, Role.EMPRESA));
        when(repository.findByCnpj(CNPJ_DIGITOS)).thenReturn(Optional.of(empresa));

        assertThat(service.buscarResponsePorCnpj(CNPJ_MASCARA, EMAIL)).isNotNull();
        verify(repository).findByCnpj(CNPJ_DIGITOS);
    }

    @Test
    void buscarResponsePorCnpj_cnpjInvalido_naoConsultaBanco() {
        assertThatThrownBy(() -> service.buscarResponsePorCnpj("abc", EMAIL)).isInstanceOf(ApiException.class);
        verifyNoInteractions(repository);
    }

    @Test
    void listarTodas_mapeiaCadaCampoNoLugarCerto() {
        Empresa e = new Empresa();
        e.setId(1L); e.setNome("Empresa Demo"); e.setCnpj(CNPJ_DIGITOS); e.setEmailContato("contato@demo.local");
        e.setTelefone("11999990000"); e.setNomeFantasia("Demo"); e.setCep("01001000"); e.setLogradouro("Rua Exemplo");
        e.setNumero("100"); e.setBairro("Centro"); e.setMunicipio("Sao Paulo"); e.setUf("SP");
        when(repository.findAll()).thenReturn(java.util.List.of(e));
        usuario(9L, EMAIL, Role.ADMIN);

        var r = service.listarDoUsuario(EMAIL).get(0);

        assertThat(r.getTelefone()).isEqualTo("11999990000");
        assertThat(r.getFantasia()).isEqualTo("Demo");
        assertThat(r.getCep()).isEqualTo("01001000");
        assertThat(r.getLogradouro()).isEqualTo("Rua Exemplo");
        assertThat(r.getNumero()).isEqualTo("100");
        assertThat(r.getBairro()).isEqualTo("Centro");
        assertThat(r.getMunicipio()).isEqualTo("Sao Paulo");
        assertThat(r.getUf()).isEqualTo("SP");
    }

    // ---------- ownership (slice 1) ----------

    @Test
    void listarDoUsuario_empresaVeSoAsDela() {
        usuario(1L, EMAIL, Role.EMPRESA);
        when(repository.findByDonoId(1L)).thenReturn(java.util.List.of());

        assertThat(service.listarDoUsuario(EMAIL)).isEmpty();
        verify(repository).findByDonoId(1L);
        verify(repository, never()).findAll();
    }

    @Test
    void buscarResponsePorCnpj_empresaDeOutroDono_lanca403AcessoNegado() {
        Empresa empresa = new Empresa();
        empresa.setCnpj(CNPJ_DIGITOS);
        Usuario dono = new Usuario(); dono.setId(1L);
        empresa.setDono(dono);
        usuario(2L, OUTRO, Role.EMPRESA);
        when(repository.findByCnpj(CNPJ_DIGITOS)).thenReturn(Optional.of(empresa));

        assertThatThrownBy(() -> service.buscarResponsePorCnpj(CNPJ_DIGITOS, OUTRO))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo(ErrorCode.ACESSO_NEGADO);
    }

    @Test
    void verificarDono_empresaSemDono_lanca403() {
        usuario(1L, EMAIL, Role.EMPRESA);
        assertThatThrownBy(() -> service.verificarDono(new Empresa(), EMAIL))
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo(ErrorCode.ACESSO_NEGADO);
    }

    @Test
    void verificarDono_adminPassa() {
        Empresa empresa = new Empresa();
        empresa.setDono(new Usuario());
        usuario(9L, EMAIL, Role.ADMIN);
        service.verificarDono(empresa, EMAIL); // nao lanca
    }
}