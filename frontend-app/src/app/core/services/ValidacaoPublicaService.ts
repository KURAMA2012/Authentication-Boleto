import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

/** Prefixo das rotas publicas da API (sem JWT). */
export const PREFIXO_API_PUBLICA = '/api/public/';

export interface ValidacaoPublicaPayload {
  codigoBoleto: string;
  emailPagador: string;
  cnpjBeneficiario?: string;
}

export interface ValidacaoPublicaResultado {
  autentico: boolean;
  veredito: 'AUTENTICO' | 'SUSPEITO';
  motivo: string;
  tokenEnviado: boolean;
}

/** Envelope padrao da API: {success, code?, message, data, timestamp}. */
export interface ApiEnvelope<T> {
  success: boolean;
  code?: string;
  message: string;
  data: T;
  timestamp?: string;
}

@Injectable({ providedIn: 'root' })
export class ValidacaoPublicaService {
  private baseUrl = `${environment.apiBaseUrl}/api/public/boleto`;

  constructor(private http: HttpClient) {}

  validar(payload: ValidacaoPublicaPayload): Observable<ApiEnvelope<ValidacaoPublicaResultado>> {
    return this.http.post<ApiEnvelope<ValidacaoPublicaResultado>>(`${this.baseUrl}/validar`, payload);
  }

  enviarToken(codigoBoleto: string, emailPagador: string): Observable<ApiEnvelope<any>> {
    return this.http.post<ApiEnvelope<any>>(`${this.baseUrl}/enviar-token`, { codigoBoleto, emailPagador });
  }

  confirmar(codigoBoleto: string, token: string): Observable<ApiEnvelope<any>> {
    return this.http.post<ApiEnvelope<any>>(`${this.baseUrl}/confirmar`, { codigoBoleto, token });
  }
}