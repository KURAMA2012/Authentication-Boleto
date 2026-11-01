package com.A3.projeto.Faculdade.AuthenticationBoleto.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Boleto;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.BoletoLog;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Empresa;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.StatusBoleto;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Role;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Usuario;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.BoletoLogRepository;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.BoletoRepository;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.EmpresaRepository;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.UsuarioRepository;

/**
 * Schema vem do ddl-auto=update (sem Flyway). Este seed garante dono para dados antigos:
 * cria o usuario demo (ficticio) se nao existir e o torna dono das empresas com dono null.
 * Tambem semeia (uma unica vez; marcador = boleto DEMO-AUTENTICO-0001) uma empresa demo,
 * alguns boletos e ~30 dias de historico de BoletoLog para o dashboard. Dados 100% ficticios.
 * Desligue com app.demo.seed-enabled=false.
 */
@Component
public class DemoDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoletoRepository boletoRepository;
    private final BoletoLogRepository boletoLogRepository;
    private final boolean habilitado;
    private final String email;
    private final String senha;

    public DemoDataSeeder(UsuarioRepository usuarioRepository, EmpresaRepository empresaRepository,
                          PasswordEncoder passwordEncoder,
                          BoletoRepository boletoRepository, BoletoLogRepository boletoLogRepository,
                          @Value("${app.demo.seed-enabled:true}") boolean habilitado,
                          @Value("${app.demo.email:demo@authboleto.local}") String email,
                          @Value("${app.demo.senha:Demo@12345}") String senha) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
        this.boletoRepository = boletoRepository;
        this.boletoLogRepository = boletoLogRepository;
        this.habilitado = habilitado;
        this.email = email;
        this.senha = senha;
    }

    @Override
    public void run(String... args) {
        if (!habilitado) return;
        Usuario demo = usuarioRepository.findByEmail(email).orElseGet(() -> {
            Usuario u = new Usuario("Usu\u00e1rio Demo", email, passwordEncoder.encode(senha), null, null);
            u.setRole(Role.EMPRESA);
            log.info("Seed: usuario demo {} criado.", email);
            return usuarioRepository.save(u);
        });
        List<Empresa> semDono = empresaRepository.findByDonoIsNull();
        semDono.forEach(e -> e.setDono(demo));
        empresaRepository.saveAll(semDono);
        if (!semDono.isEmpty()) log.info("Seed: {} empresa(s) sem dono atribuidas a {}.", semDono.size(), email);
        semearHistorico(demo);
    }

    public static final String DEMO_CODIGO = "DEMO-AUTENTICO-0001";
    public static final String DEMO_CODIGO_BARRAS = "23790000100000000000000000000000000000000001";
    public static final String DEMO_CNPJ = "11222333000181";

    /** Idempotente: se o boleto marcador ja existe, nao faz nada. */
    void semearHistorico(Usuario demo) {
        if (boletoRepository.findByCodigoAutenticacao(DEMO_CODIGO) != null) return;
        Empresa empresa = empresaRepository.findByCnpj(DEMO_CNPJ).orElseGet(() -> {
            Empresa e = new Empresa();
            e.setNome("Empresa Demo Autentica Ltda");
            e.setNomeFantasia("Demo Autentica");
            e.setCnpj(DEMO_CNPJ);
            e.setEmailContato("contato@demo.local");
            e.setMunicipio("Cidade Demo");
            e.setUf("SP");
            return e;
        });
        if (empresa.getDono() == null) empresa.setDono(demo);
        empresa = empresaRepository.save(empresa);

        LocalDateTime agora = LocalDateTime.now();
        List<Boleto> boletos = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Boleto b = new Boleto();
            b.setCodigoAutenticacao(i == 1 ? DEMO_CODIGO : String.format("DEMO-BOLETO-%04d", i));
            b.setCodigoBarras(i == 1 ? DEMO_CODIGO_BARRAS : String.format("2379000010000000000000000000000000000000%04d", i));
            b.setValor(new BigDecimal(100 * i + ".00"));
            b.setVencimento(LocalDate.now().plusDays(10L * i));
            b.setDataGeracao(agora.minusDays(6L * i));
            b.setCnpjBeneficiario(DEMO_CNPJ);
            b.setStatus(StatusBoleto.GERADO);
            b.setAutenticado(false);
            b.setEmpresa(empresa);
            b.setNomePagador("Pagador Demo " + i);
            boletos.add(boletoRepository.save(b));
        }

        Random rnd = new Random(42);
        List<BoletoLog> logs = new ArrayList<>();
        for (int d = 29; d >= 0; d--) {
            int qtd = 2 + rnd.nextInt(5);
            for (int k = 0; k < qtd; k++) {
                BoletoLog l = new BoletoLog();
                l.setIpUsuario("192.0.2." + (1 + rnd.nextInt(200)));
                l.setDataConsulta(agora.minusDays(d).withHour(8 + rnd.nextInt(10)).withMinute(rnd.nextInt(60)));
                if (rnd.nextInt(10) < 7) {
                    Boleto b = boletos.get(rnd.nextInt(boletos.size()));
                    l.setCodigoAutenticacao(b.getCodigoAutenticacao());
                    l.setBoleto(b);
                    l.setValido(true);
                    l.setMensagem("Boleto autenticado com sucesso.");
                    if (rnd.nextBoolean()) l.setCnpjInformado(DEMO_CNPJ);
                } else {
                    l.setCodigoAutenticacao(String.format("FALSO-%05d", rnd.nextInt(100000)));
                    l.setValido(false);
                    l.setMensagem("Tentativa rejeitada: boleto n\u00e3o encontrado.");
                    l.setCnpjInformado(DEMO_CNPJ);
                }
                logs.add(l);
            }
        }
        boletoLogRepository.saveAll(logs);
        log.info("Seed: empresa demo {}, {} boletos e {} logs (30 dias) criados.", DEMO_CNPJ, boletos.size(), logs.size());
    }
}