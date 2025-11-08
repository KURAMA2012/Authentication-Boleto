package com.A3.projeto.Faculdade.AuthenticationBoleto.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoRequest;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Boleto;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Empresa;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.BoletoNaoEncontradoException;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.BoletoRepository;

import jakarta.transaction.Transactional;

@Service
public class BoletoService {
 
    
    @Autowired
    private BoletoRepository boletoRepository;
    
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
    	
    	Empresa empresa = empresaService.buscarPorCnpj(request.getCnpjEmpresa());
    	
        Boleto boleto = new Boleto();
        boleto.setValor(request.getValor());
        boleto.setVencimento(request.getDataVencimento());
        boleto.setCnpjBeneficiario(request.getCnpjEmpresa());
        boleto.setDataGeracao(LocalDateTime.now());
        boleto.setEmpresa(empresa);
        
        
        boleto.setAutenticado(false);

        // gera códigos
        boleto.setCodigoAutenticacao(UUID.randomUUID().toString());
        boleto.setCodigoBarras(request.getCodigoBarras());
        boleto.setTokenVerificacao(UUID.randomUUID().toString());

        return boletoRepository.save(boleto);
    }
    
    public Boleto atualizarBoleto(Boleto boleto) {
        return boletoRepository.save(boleto);
    }
    
    @Transactional
    public String gerarOuRecuperarToken(String codigoAutenticacao) {
        Boleto boleto = boletoRepository.findByCodigoAutenticacao(codigoAutenticacao);
        if (boleto == null) {
            throw new BoletoNaoEncontradoException("Boleto não encontrado.");
        }

        // Se já existe token, reutiliza. Se não, gera novo.
        if (boleto.getTokenVerificacao() == null || boleto.getTokenVerificacao().isBlank()) {
            boleto.setTokenVerificacao(UUID.randomUUID().toString());
            boletoRepository.save(boleto);
        }

        return boleto.getTokenVerificacao();
    }

    public boolean confirmarToken(String codigoAutenticacao, String tokenVerificacao) {
        Boleto boleto = boletoRepository.findByCodigoAutenticacao(codigoAutenticacao);
        if (boleto == null) return false;

        if (!tokenVerificacao.equals(boleto.getTokenVerificacao())) return false;

        boleto.setAutenticado(true);
        boletoRepository.save(boleto);
        return true;
    }
    
    

}