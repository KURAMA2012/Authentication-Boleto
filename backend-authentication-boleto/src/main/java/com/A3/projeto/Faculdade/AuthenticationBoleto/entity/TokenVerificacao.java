package com.A3.projeto.Faculdade.AuthenticationBoleto.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tab_token_verificacao")
public class TokenVerificacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String token;


	@Column(nullable = false)
	private LocalDateTime dataCriacao;

	@Column(nullable = false)
	private LocalDateTime dataExpiracao;

	private boolean utilizado;


	@Enumerated(EnumType.STRING)
	private StatusToken status;

	@ManyToOne
	@JoinColumn(name = "boleto_id", nullable = false)
	private Boleto boleto;

	public TokenVerificacao() {
	}

	public TokenVerificacao(String token, LocalDateTime dataCriacao, LocalDateTime dataExpiracao, Boleto boleto) {
		this.token = token;
		this.dataCriacao = dataCriacao;
		this.dataExpiracao = dataExpiracao;
		this.status = StatusToken.ATIVO;
		this.utilizado = false;
		this.boleto = boleto;
	}

	// --- Getters e Setters ---
	public Long getId() {
		return id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public LocalDateTime getDataExpiracao() {
		return dataExpiracao;
	}

	public void setDataExpiracao(LocalDateTime dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}

	public boolean isUtilizado() {
		return utilizado;
	}

	public void setUtilizado(boolean utilizado) {
		this.utilizado = utilizado;
	}

	public StatusToken getStatus() {
		return status;
	}

	public void setStatus(StatusToken status) {
		this.status = status;
	}

	public Boleto getBoleto() {
		return boleto;
	}

	public void setBoleto(Boleto boleto) {
		this.boleto = boleto;
	}
}