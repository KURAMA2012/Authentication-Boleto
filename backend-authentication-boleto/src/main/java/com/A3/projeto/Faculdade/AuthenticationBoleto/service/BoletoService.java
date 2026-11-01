package com.A3.projeto.Faculdade.AuthenticationBoleto.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.A3.projeto.Faculdade.AuthenticationBoleto.listener.BoletoBatchListener;
import com.A3.projeto.Faculdade.AuthenticationBoleto.mapper.BoletoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.Cnpj;
import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ErrorCode;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoRequest;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoValidacaoResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.ApiException;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Boleto;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Empresa;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.StatusBoleto;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.StatusToken;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.TokenVerificacao;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.BoletoNaoEncontradoException;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.BoletoRepository;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.TokenVerificacaoRepository;

import jakarta.transaction.Transactional;

@Service
public class BoletoService {


    private static final Logger logger = LoggerFactory.getLogger(BoletoBatchListener.class);

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private BoletoMapper boletoMapper;
    
    @Autowired
    private  TokenVerificacaoRepository tokenVerificacaoRepository;

    @Autowired
    private TokenEmailService tokenEmailService;
    
    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private BoletoLogService boletoLogService;

    /** Tentativas erradas de /confirmar antes de invalidar o token (429 LIMITE_TENTATIVAS). */
    @Value("${app.token.max-tentativas:5}")
    private int maxTentativas = 5;
    
    
    public boolean validarCodigoAutenticacao(String codigo) {
        Boleto boleto = boletoRepository.findByCodigoAutenticacao(codigo);
        return boleto != null; // se achou, é válido
    }
    
    public Boleto buscarPorCodigoAutenticacao(String codigoAutenticacao) {
    	
    	Boleto boleto = boletoRepository.buscarPorCodigoAutenticacao(codigoAutenticacao);
    	if(boleto == null) {
    		throw new BoletoNaoEncontradoException("Boleto não encontrado ou código inválido.");
    	}
        return boleto; 
    }
    
    public Boleto buscarPorCodigoBarras(String codigoBarras) {
    	Boleto boleto = boletoRepository.buscarPorCodigoBarras(codigoBarras);
    	if(boleto == null) {
    		throw new BoletoNaoEncontradoException("Boleto não encontrado.");
    	}
    	
        return boletoRepository.buscarPorCodigoBarras(codigoBarras);
    }


    /** 403 ACESSO_NEGADO se o boleto for de empresa de outro dono. */
    public void verificarDono(Boleto boleto, String emailLogado) {
        empresaService.verificarDono(boleto.getEmpresa(), emailLogado);
    }

    public Boleto criarBoleto(BoletoRequest request, String emailLogado) {
        // So no /criar (manual). O lote (criarBoletoAPartirDoBatch) segue sem esta regra.
        if (request.getVencimento() != null && request.getVencimento().isBefore(LocalDate.now())) {
            throw new ApiException(ErrorCode.VALIDACAO_CAMPO, "vencimento deve ser hoje ou futuro");
        }
    	
    	Optional<Boleto> existente = Optional.ofNullable( boletoRepository.buscarPorCodigoBarras(request.getCodigoBarras()));

    	        if (existente.isPresent()) {
    	            throw new IllegalArgumentException(
    	                "Já existe um boleto registrado com o código de barras informado: "
    	                + request.getCodigoBarras()
    	            );
    	        }
    	
    	Empresa empresa = empresaService.buscarPorCnpj(Cnpj.normalizar(request.getCnpjBeneficiario()));
    	empresaService.verificarDono(empresa, emailLogado);
    	
        Boleto boleto = boletoMapper.toEntity(request);

        boleto.setEmpresa(empresa);

        return boletoRepository.save(boleto);
    }
    
    
    @Transactional
    public void criarBoletoAPartirDoBatch(Boleto boleto) {
    	Boleto existe = boletoRepository.buscarPorCodigoBarras(boleto.getCodigoBarras());
        if (existe != null) {
            logger.warn("⚠️ Boleto já existe: " + boleto.getCodigoBarras());
            return;
        }
        
        boleto.setStatus(StatusBoleto.GERADO);
        boleto.setDataGeracao(LocalDateTime.now());
        boleto.setTokenVerificacao(UUID.randomUUID().toString());
        boleto.setCodigoAutenticacao(UUID.randomUUID().toString());

        boletoRepository.save(boleto);
    }

    
    public Boleto atualizarBoleto(Boleto boleto) {
        return boletoRepository.save(boleto);
    }
    
    /**
     * Validacao publica do pagador. Nunca lanca "nao encontrado": responde SUSPEITO (200)
     * para nao revelar dados. Toda tentativa fica em BoletoLog (com o cnpjInformado, se houver).
     */
    /** codigoBoleto do Front pode ser o codigo de autenticacao ou o de barras. */
    public String resolverCodigoAutenticacao(String codigo) {
        if (codigo == null || codigo.isBlank()) return codigo;
        Boleto porBarras = boletoRepository.buscarPorCodigoBarras(codigo.trim());
        return porBarras != null ? porBarras.getCodigoAutenticacao() : codigo.trim();
    }

    public BoletoValidacaoResponse validarPublico(String codigoAutenticacao, String cnpjInformadoBruto, String ip) {
        String cnpjInformado = (cnpjInformadoBruto == null || cnpjInformadoBruto.isBlank())
                ? null : Cnpj.normalizar(cnpjInformadoBruto);
        Boleto boleto = (codigoAutenticacao == null || codigoAutenticacao.isBlank())
                ? null : boletoRepository.buscarPorCodigoAutenticacao(codigoAutenticacao);

        if (boleto == null) {
            String motivo = "C\u00f3digo de autentica\u00e7\u00e3o n\u00e3o encontrado.";
            boletoLogService.registrarLog(codigoAutenticacao, ip, false, "Tentativa rejeitada: boleto n\u00e3o encontrado.", null, cnpjInformado);
            return BoletoValidacaoResponse.suspeito(motivo);
        }

        String cnpjEmpresa = boleto.getEmpresa() != null ? boleto.getEmpresa().getCnpj() : boleto.getCnpjBeneficiario();
        if (cnpjInformado != null && !cnpjInformado.equals(cnpjEmpresa)) {
            String motivo = "O CNPJ informado n\u00e3o corresponde ao benefici\u00e1rio do boleto.";
            boletoLogService.registrarLog(codigoAutenticacao, ip, false, "Tentativa suspeita: " + motivo, boleto, cnpjInformado);
            return BoletoValidacaoResponse.suspeito(motivo);
        }

        boleto.setAutenticado(true);
        boleto.setStatus(StatusBoleto.AUTENTICADO);
        boletoRepository.save(boleto);
        boletoLogService.registrarLog(codigoAutenticacao, ip, true, "Boleto autenticado com sucesso.", boleto, cnpjInformado);
        return BoletoValidacaoResponse.autentico("Boleto emitido por empresa cadastrada.");
    }

    /** Envia o token para o e-mail informado pelo pagador (reenvia o ativo, se ainda valido). */
    @Transactional
    public String gerarOuRecuperarToken(String codigoAutenticacao, String emailPagador) {
        if (emailPagador == null || !emailPagador.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ApiException(ErrorCode.VALIDACAO_CAMPO, "Informe um e-mail v\u00e1lido para receber o token.");
        }
        Boleto boleto = boletoRepository.findByCodigoAutenticacao(codigoAutenticacao);
                
    		if(boleto == null) {
        		throw new BoletoNaoEncontradoException("Boleto não encontrado.");
        	}

        Optional<TokenVerificacao> tokenExistente = tokenVerificacaoRepository.findByBoletoAndUtilizadoFalse(boleto);
        if (tokenExistente.isPresent()) {
            TokenVerificacao existente = tokenExistente.get();
            if (existente.getDataExpiracao().isAfter(LocalDateTime.now())) {
                tokenEmailService.enviarToken(emailPagador, boleto.getCodigoAutenticacao(), existente.getToken());
                return existente.getToken(); // reutiliza se ainda estiver válido
            }
        }

        String tokenGerado = String.format("%06d", (int) (Math.random() * 1_000_000));


        TokenVerificacao novoToken = new TokenVerificacao(
                tokenGerado,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5),
                boleto
        );

        tokenVerificacaoRepository.save(novoToken);
        System.out.printf("✅ Token gerado para boleto %s: %s%n", boleto.getCodigoAutenticacao(), tokenGerado);

        boleto.setTokenVerificacao(tokenGerado);
        boletoRepository.save(boleto);

        tokenEmailService.enviarToken(emailPagador, boleto.getCodigoAutenticacao(), tokenGerado);

        return tokenGerado;
    }

    @Transactional(dontRollbackOn = ApiException.class) // o bloqueio do token precisa ser gravado
    public boolean confirmarToken(String codigoAutenticacao, String tokenVerificacao) {
        Boleto boleto = boletoRepository.findByCodigoAutenticacao(codigoAutenticacao);
        
		if(boleto == null) {
    		return false; // boleto inexistente: mesmo tratamento de token invalido (401), sem revelar existencia
    	}

        Optional<TokenVerificacao> token = tokenVerificacaoRepository.findByBoletoAndUtilizadoFalse(boleto);

        if (token.isEmpty()) return false;

        TokenVerificacao existente = token.get();

        if (existente.getDataExpiracao().isBefore(LocalDateTime.now()))
            return false;

        if (!existente.getToken().equals(tokenVerificacao)) {
            int tentativas = existente.getTentativas() + 1;
            existente.setTentativas(tentativas);
            if (tentativas >= maxTentativas) {
                // Bloqueia o token: o pagador precisa pedir um novo em /enviar-token.
                existente.setUtilizado(true);
                existente.setStatus(StatusToken.CANCELADO);
                tokenVerificacaoRepository.save(existente);
                throw new ApiException(ErrorCode.LIMITE_TENTATIVAS,
                        "Limite de tentativas atingido. Solicite um novo token.");
            }
            tokenVerificacaoRepository.save(existente);
            return false;
        }

        // Marca como utilizado e altera status
        existente.setUtilizado(true);
        existente.setStatus(StatusToken.UTILIZADO);
        tokenVerificacaoRepository.save(existente);

        boleto.setStatus(StatusBoleto.AUTENTICADO);
        boleto.setAutenticado(true);
        boletoRepository.save(boleto);

        return true;
    }
    
    

}
