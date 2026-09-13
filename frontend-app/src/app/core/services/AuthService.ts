import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NavController } from '@ionic/angular';
import { Observable, tap } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = `${environment.apiBaseUrl}/api/auth`;
  private storageKey = 'usuario';

  constructor(
    private http: HttpClient,
    private navCtrl: NavController
  ) {}

  cadastrar(user: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/cadastrar`, user);
  }

  login(email: string, senha: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/login?email=${email}&senha=${senha}`, {})
      .pipe(
        tap((resp: any) => {
          if (resp.success) {
            localStorage.setItem(this.storageKey, JSON.stringify(resp.data));
          }
        })
      );
  }

  getUsuario() {
    const data = localStorage.getItem(this.storageKey);
    return data ? JSON.parse(data) : null;
  }

  isLogged(): boolean {
    return localStorage.getItem(this.storageKey) !== null;
  }

  logout() {
    localStorage.removeItem(this.storageKey);
    this.navCtrl.navigateRoot('/login');
  }

}
