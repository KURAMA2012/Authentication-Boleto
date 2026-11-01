package com.A3.projeto.Faculdade.AuthenticationBoleto.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Boleto;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Empresa;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Usuario;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.BoletoLogRepository;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.BoletoRepository;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.EmpresaRepository;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.UsuarioRepository;

class DemoDataSeederTest {

    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final EmpresaRepository empresas = mock(EmpresaRepository.class);
    private final BoletoRepository boletos = mock(BoletoRepository.class);
    private final BoletoLogRepository logs = mock(BoletoLogRepository.class);
    private final DemoDataSeeder seeder = new DemoDataSeeder(usuarios, empresas, mock(PasswordEncoder.class),
            boletos, logs, true, "demo@authboleto.local", "x");

    @Test
    void naoSemeiaDeNovoQuandoMarcadorExiste() {
        when(boletos.findByCodigoAutenticacao(DemoDataSeeder.DEMO_CODIGO)).thenReturn(new Boleto());
        seeder.semearHistorico(new Usuario());
        verify(boletos, never()).save(any());
        verifyNoInteractions(logs);
    }

    @Test
    void semeiaBoletoConhecidoEHistoricoNaPrimeiraVez() {
        when(empresas.findByCnpj(DemoDataSeeder.DEMO_CNPJ)).thenReturn(Optional.empty());
        when(empresas.save(any())).thenAnswer(i -> i.getArgument(0));
        when(boletos.save(any())).thenAnswer(i -> i.getArgument(0));
        seeder.semearHistorico(new Usuario());
        verify(boletos).save(argThat(b -> DemoDataSeeder.DEMO_CODIGO.equals(b.getCodigoAutenticacao())
                && DemoDataSeeder.DEMO_CNPJ.equals(b.getCnpjBeneficiario())));
        verify(boletos, times(5)).save(any());
        verify(logs).saveAll(anyList());
        verify(empresas).save(argThat((Empresa e) -> e.getDono() != null));
    }
}