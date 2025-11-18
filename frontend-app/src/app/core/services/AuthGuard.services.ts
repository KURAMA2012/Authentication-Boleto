import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from './AuthService';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private auth: AuthService, private router: Router) {}

  canActivate() {
    const logged = this.auth.isLogged();

    if (!logged) {
      this.router.navigate(['/login']);
      return false;
    }

    return true;
  }
  
}
