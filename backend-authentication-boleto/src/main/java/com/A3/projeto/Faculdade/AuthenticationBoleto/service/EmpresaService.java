package com.A3.projeto.Faculdade.AuthenticationBoleto.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.EmpresaRequest;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.EmpresaResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Empresa;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.EmpresaNaoEncontradaException;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.EmpresaRepository;

@Service
public class EmpresaService {

	private final EmpresaRepository repository;

	public EmpresaService(EmpresaRepository repository) {
		this.repository = repository;
	}

	public EmpresaResponse salvar(EmpresaRequest request) {
		Empresa empresa = new Empresa();
		empresa.setNome(request.getNome());
		empresa.setCnpj(request.getCnpj());
		empresa.setEmailContato(request.getEmailContato());
		empresa.setTelefone(request.getTelefone());
		empresa.setNomeFantasia(request.getFantasia());
		empresa.setCep(request.getCep());
		empresa.setLogradouro(request.getLogradouro());
		empresa.setNumero(request.getNumero());
		empresa.setBairro(request.getBairro());
		empresa.setMunicipio(request.getMunicipio());
		empresa.setUf(request.getUf());

		Empresa salva = repository.save(empresa);
		return new EmpresaResponse(salva.getId(), salva.getNome(), salva.getCnpj(), salva.getEmailContato(),
				salva.getTelefone(), salva.getNomeFantasia(), salva.getCep(), salva.getLogradouro(), salva.getNumero(),
				salva.getBairro(), salva.getMunicipio(), salva.getUf());
	}

	public List<EmpresaResponse> listarTodas() {
		return repository.findAll().stream()
				.map(e -> new EmpresaResponse(e.getId(), e.getNome(), e.getCnpj(), e.getEmailContato(), e.getTelefone(),
						e.getTelefone(), e.getNomeFantasia(), e.getCep(), e.getLogradouro(), e.getNumero(),
						e.getBairro(), e.getMunicipio()))
				.collect(Collectors.toList());
	}

	public EmpresaResponse buscarResponsePorCnpj(String cnpj) {
		Empresa empresa = repository.findByCnpj(cnpj)
				.orElseThrow(() -> new EmpresaNaoEncontradaException("Empresa com CNPJ " + cnpj + " não encontrada"));
		return new EmpresaResponse(empresa.getId(), empresa.getNome(), empresa.getCnpj(), empresa.getEmailContato(),
				empresa.getTelefone(), empresa.getTelefone(), empresa.getNomeFantasia(), empresa.getCep(),
				empresa.getLogradouro(), empresa.getNumero(), empresa.getBairro(), empresa.getMunicipio());
	}

	public Empresa buscarPorCnpj(String cnpj) {
		return repository.findByCnpj(cnpj)
				.orElseThrow(() -> new EmpresaNaoEncontradaException("Empresa com CNPJ " + cnpj + " não encontrada"));
	}

}