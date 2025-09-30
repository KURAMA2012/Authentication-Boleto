package auth.dto;

public class LoginResponseDTO {
	private String token;
	private String nomeUsuario;

	public LoginResponseDTO(String token, String nomeUsuario) {
		this.token = token;
		this.nomeUsuario = nomeUsuario;
	}

	public String getToken() {
		return token;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}
}