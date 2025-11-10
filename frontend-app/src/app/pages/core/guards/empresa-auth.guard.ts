import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class EmpresaAuthGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(): boolean {
    const empresa = localStorage.getItem('empresa');

    if (empresa) {
      return true; //Empresa autenticada
    }

    this.router.navigate(['/cadastro-empresa']);
    return false;
  }
}
