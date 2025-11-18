import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AccessDigitalService {

  setEnabled(email: string) {
    localStorage.setItem('acessoDigitalHabilitado', 'true');
    localStorage.setItem('acessoDigitalEmail', email);
  }

  disable() {
    localStorage.setItem('acessoDigitalHabilitado', 'false');
  }

  isEnabled(): boolean {
    return localStorage.getItem('acessoDigitalHabilitado') === 'true';
  }

  getEmail(): string | null {
    return localStorage.getItem('acessoDigitalEmail');
  }
}
