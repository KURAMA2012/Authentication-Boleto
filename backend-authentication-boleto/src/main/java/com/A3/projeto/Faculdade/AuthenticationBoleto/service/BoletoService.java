package com.A3.projeto.Faculdade.AuthenticationBoleto.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.A3.projeto.Faculdade.AuthenticationBoleto.listener.BoletoBatchListener;
import com.A3.projeto.Faculdade.AuthenticationBoleto.mapper.BoletoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoRequest;
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
    private EmpresaService empresaService;
    
    
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


    public Boleto criarBoleto(BoletoRequest request) {
    	
    	Optional<Boleto> existente = Optional.ofNullable( boletoRepository.buscarPorCodigoBarras(request.getCodigoBarras()));

    	        if (existente.isPresent()) {
    	            throw new IllegalArgumentException(
    	                "Já existe um boleto registrado com o código de barras informado: "
    	                + request.getCodigoBarras()
    	            );
    	        }
    	
    	Empresa empresa = empresaService.buscarPorCnpj(request.getCnpjBeneficiario());
    	
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
    
    @Transactional
    public String gerarOuRecuperarToken(String codigoAutenticacao) {
        Boleto boleto = boletoRepository.findByCodigoAutenticacao(codigoAutenticacao);
                
    		if(boleto == null) {
        		throw new BoletoNaoEncontradoException("Boleto não encontrado.");
        	}

        Optional<TokenVerificacao> tokenExistente = tokenVerificacaoRepository.findByBoletoAndUtilizadoFalse(boleto);
        if (tokenExistente.isPresent()) {
            TokenVerificacao existente = tokenExistente.get();
            if (existente.getDataExpiracao().isAfter(LocalDateTime.now())) {
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

        // Implementar(Estudar ideia)
        // enviar token via SMS/email
        // smsService.enviarSMS(boleto.getTelefonePagador(), "Seu token é: " + tokenGerado);

        return tokenGerado;
    }

    @Transactional
    public boolean confirmarToken(String codigoAutenticacao, String tokenVerificacao) {
        Boleto boleto = boletoRepository.findByCodigoAutenticacao(codigoAutenticacao);
        
		if(boleto == null) {
    		throw new BoletoNaoEncontradoException("Boleto não encontrado.");
    	}

        Optional<TokenVerificacao> token = tokenVerificacaoRepository.findByBoletoAndUtilizadoFalse(boleto);

        if (token.isEmpty()) return false;

        TokenVerificacao existente = token.get();

        if (existente.getDataExpiracao().isBefore(LocalDateTime.now()))
            return false;

        if (!existente.getToken().equals(tokenVerificacao))
            return false;

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
