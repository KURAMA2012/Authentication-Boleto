package com.A3.projeto.Faculdade.AuthenticationBoleto.dto;

public class EmpresaResponse {
	private Long id;
	private String nome;
	private String cnpj;
	private String emailContato;
	private String telefone;
	private String fantasia;
	private String cep;
	private String logradouro;
	private String numero;
	private String bairro;
	private String municipio;
	private String uf;

	public EmpresaResponse(Long id, String nome, String cnpj, String emailContato, String telefone, String fantasia,
			String cep, String logradouro, String numero, String bairro, String municipio, String uf) {
		super();
		this.id = id;
		this.nome = nome;
		this.cnpj = cnpj;
		this.emailContato = emailContato;
		this.telefone = telefone;
		this.fantasia = fantasia;
		this.cep = cep;
		this.logradouro = logradouro;
		this.numero = numero;
		this.bairro = bairro;
		this.municipio = municipio;
		this.uf = uf;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFantasia() {
		return fantasia;
	}

	public void setFantasia(String fantasia) {
		this.fantasia = fantasia;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public void setEmailContato(String emailContato) {
		this.emailContato = emailContato;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
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