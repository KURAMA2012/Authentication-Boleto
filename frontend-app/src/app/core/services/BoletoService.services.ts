import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BoletoService {
  private mainApiUrl = 'http://localhost:8080/api/boleto'; // serviço principal
  private batchApiUrl = 'http://localhost:8082/api/boleto'; // serviço batch

  constructor(private http: HttpClient) {}

  criarBoleto(request: any): Observable<any> {
    return this.http.post(`${this.mainApiUrl}/criar`, request);
  }

  buscarPorCodigoBarras(codigoBarras: string): Observable<any> {
    return this.http.get(`${this.mainApiUrl}/buscar/${codigoBarras}`);
  }

  confirmarToken(codigoAutenticacao: string, tokenVerificacao: string): Observable<any> {
    return this.http.post(`${this.mainApiUrl}/confirmar`, {
      codigoAutenticacao,
      tokenVerificacao
    });
  }

  validarBoleto(codigoAutenticacao: string): Observable<any> {
    return this.http.post(`${this.mainApiUrl}/validar`, { codigoAutenticacao });
  }

  enviarToken(codigoAutenticacao: string): Observable<any> {
    return this.http.post(`${this.mainApiUrl}/enviar-token`, { codigoAutenticacao });
  }

uploadCsv(file: File): Observable<HttpEvent<any>> {
  const formData = new FormData();
  formData.append('file', file, file.name);

  return this.http.post<HttpEvent<any>>(
    `${this.batchApiUrl}/upload`,
    formData,
    {
      reportProgress: true,
      observe: 'events',
      responseType: 'text' as 'json'
    }
  );
}


  getLogs(): Observable<any> {
    return this.http.get(`${this.mainApiUrl}/logs`);
  }
}
