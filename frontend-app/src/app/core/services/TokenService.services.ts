import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TokenService {
  private apiUrl = 'http://localhost:8080/token';

  constructor(private http: HttpClient) {}

  validarBoleto(codigoAutenticacao: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/validar`, { codigoAutenticacao });
  }

  enviarToken(codigoAutenticacao: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/enviar-token`, { codigoAutenticacao });
  }
}
