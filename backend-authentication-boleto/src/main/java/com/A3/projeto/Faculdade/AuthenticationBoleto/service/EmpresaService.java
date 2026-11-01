package com.A3.projeto.Faculdade.AuthenticationBoleto.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.A3.projeto.Faculdade.AuthenticationBoleto.mapper.EmpresaMapper;
import org.springframework.stereotype.Service;

import com.A3.projeto.Faculdade.AuthenticationBoleto.common.Cnpj;
import com.A3.projeto.Faculdade.AuthenticationBoleto.common.ErrorCode;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.EmpresaRequest;
import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.EmpresaResponse;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Empresa;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Usuario;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.ApiException;
import com.A3.projeto.Faculdade.AuthenticationBoleto.exception.EmpresaNaoEncontradaException;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.EmpresaRepository;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.UsuarioRepository;

@Service
public class EmpresaService {

	private final EmpresaRepository repository;
	private final EmpresaMapper empresaMapper;
	private final UsuarioRepository usuarioRepository;

	public EmpresaService(EmpresaRepository repository, EmpresaMapper empresaMapper, UsuarioRepository usuarioRepository) {
		this.repository = repository;
		this.empresaMapper = empresaMapper;
		this.usuarioRepository = usuarioRepository;
	}

	/** Cadastra a empresa tendo como dono o usuario logado. */
	public EmpresaResponse salvar(EmpresaRequest request, String emailLogado) {
		request.setCnpj(Cnpj.normalizar(request.getCnpj()));

		if (repository.existsByCnpj(request.getCnpj())) {
			throw new ApiException(ErrorCode.EMPRESA_DUPLICADA, "CNPJ j\u00e1 cadastrado.");
		}

		Usuario dono = usuarioLogado(emailLogado);
		Empresa empresa = empresaMapper.toEntity(request);
		empresa.setDono(dono);

		Empresa salva = repository.save(empresa);
		return toResponse(salva);
	}

	/** EMPRESA ve so as dela; ADMIN ve todas. */
	public List<EmpresaResponse> listarDoUsuario(String emailLogado) {
		Usuario u = usuarioLogado(emailLogado);
		List<Empresa> empresas = u.isAdmin() ? repository.findAll() : repository.findByDonoId(u.getId());
		return empresas.stream().map(this::toResponse).collect(Collectors.toList());
	}

	/** So as empresas cujo dono e o usuario logado (inclusive para ADMIN). */
	public List<EmpresaResponse> listarMinhas(String emailLogado) {
		Usuario u = usuarioLogado(emailLogado);
		return repository.findByDonoId(u.getId()).stream().map(this::toResponse).collect(Collectors.toList());
	}

	public EmpresaResponse buscarResponsePorCnpj(String cnpjInformado, String emailLogado) {
		String cnpj = Cnpj.normalizar(cnpjInformado);
		Empresa empresa = buscarPorCnpj(cnpj);
		verificarDono(empresa, emailLogado);
		return toResponse(empresa);
	}

	public Empresa buscarPorCnpj(String cnpj) {
		return repository.findByCnpj(cnpj)
				.orElseThrow(() -> new EmpresaNaoEncontradaException("Empresa com CNPJ " + cnpj + " n\u00e3o encontrada"));
	}

	public Empresa buscarPorId(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new EmpresaNaoEncontradaException("Empresa " + id + " n\u00e3o encontrada"));
	}

	/** 403 ACESSO_NEGADO se o usuario logado nao for o dono (ADMIN passa sempre). */
	public void verificarDono(Empresa empresa, String emailLogado) {
		Usuario u = usuarioLogado(emailLogado);
		if (u.isAdmin()) return;
		Usuario dono = empresa == null ? null : empresa.getDono();
		if (dono == null || !Objects.equals(dono.getId(), u.getId())) {
			throw new ApiException(ErrorCode.ACESSO_NEGADO, "Acesso negado: recurso de outro usu\u00e1rio.");
		}
	}

	public Usuario usuarioLogado(String email) {
		return usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new ApiException(ErrorCode.TOKEN_INVALIDO, "Usu\u00e1rio do token n\u00e3o encontrado."));
	}

	private EmpresaResponse toResponse(Empresa e) {
		return new EmpresaResponse(e.getId(), e.getNome(), e.getCnpj(), e.getEmailContato(), e.getTelefone(),
				e.getNomeFantasia(), e.getCep(), e.getLogradouro(), e.getNumero(), e.getBairro(), e.getMunicipio(),
				e.getUf());
	}

}