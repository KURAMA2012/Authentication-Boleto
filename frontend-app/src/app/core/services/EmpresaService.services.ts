import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class EmpresaService {

  private apiUrl = `${environment.apiBaseUrl}/api/empresas`;
  private consultaUrl = `${environment.apiBaseUrl}/api/consulta`;

  constructor(private http: HttpClient) {}

  cadastrarEmpresa(empresa: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/cadastrar`, empresa);
  }

  /** Empresas do usuario logado (JWT). */
  minhas(): Observable<any> {
    return this.http.get(`${this.apiUrl}/minhas`);
  }

  /** Metricas da empresa (so o dono). Datas em yyyy-MM-dd. */
  metricas(id: number, de: string, ate: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}/metricas`, { params: { de, ate } });
  }

  buscarCnpj(cnpj: string): Observable<any> {
    return this.http.get(`${this.consultaUrl}/cnpj/${cnpj}`);
  }

  buscarCep(cep: string): Observable<any> {
    return this.http.get(`${this.consultaUrl}/cep/${cep}`);
  }
}
