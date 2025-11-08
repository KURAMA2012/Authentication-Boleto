package com.A3.projeto.Faculdade.AuthenticationBoleto.dto;

public class EmpresaResponse {
	private Long id;
	private String nome;
	private String cnpj;
	private String emailContato;
	private String telefone;

	public EmpresaResponse(Long id, String nome, String cnpj, String emailContato, String telefone) {
		this.id = id;
		this.nome = nome;
		this.cnpj = cnpj;
		this.emailContato = emailContato;
		this.telefone = telefone;
	}

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getCnpj() {
		return cnpj;
	}

	public String getEmailContato() {
		return emailContato;
	}

	public String getTelefone() {
		return telefone;
	}
}