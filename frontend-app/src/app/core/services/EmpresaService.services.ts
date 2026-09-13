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

  buscarCnpj(cnpj: string): Observable<any> {
    return this.http.get(`${this.consultaUrl}/cnpj/${cnpj}`);
  }

  buscarCep(cep: string): Observable<any> {
    return this.http.get(`${this.consultaUrl}/cep/${cep}`);
  }
}
