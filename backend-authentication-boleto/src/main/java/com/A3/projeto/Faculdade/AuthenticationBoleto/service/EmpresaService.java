package com.A3.projeto.Faculdade.AuthenticationBoleto.service;

import java.util.List;
import java.util.stream.Collectors;

import com.A3.projeto.Faculdade.AuthenticationBoleto.mapper.EmpresaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.EmpresaRequest;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.EmpresaResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Empresa;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.EmpresaNaoEncontradaException;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.EmpresaRepository;

@Service
public class EmpresaService {

	@Autowired
	private final EmpresaRepository repository;

	@Autowired
	private final EmpresaMapper empresaMapper;

	public EmpresaService(EmpresaRepository repository, EmpresaMapper empresaMapper) {
		this.repository = repository;
        this.empresaMapper = empresaMapper;
    }

	public EmpresaResponse salvar(EmpresaRequest request) {
		Empresa empresa = empresaMapper.toEntity(request);

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
