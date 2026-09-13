import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class TokenService {
  private apiUrl = `${environment.apiBaseUrl}/token`;

  constructor(private http: HttpClient) {}

  validarBoleto(codigoAutenticacao: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/validar`, { codigoAutenticacao });
  }

  enviarToken(codigoAutenticacao: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/enviar-token`, { codigoAutenticacao });
  }
}
