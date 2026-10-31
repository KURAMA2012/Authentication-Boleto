import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

/**
 * Envia o JWT (usuario.token salvo no login) como Authorization: Bearer.
 * Rotas /api/auth/** ficam publicas. Em 401 fora do login, limpa a sessao e volta ao /login.
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private readonly storageKey = 'usuario';

  constructor(private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const publica = req.url.includes('/api/auth/');
    const headers: Record<string, string> = { Accept: 'application/json' };

    if (!(req.body instanceof FormData)) {
      headers['Content-Type'] = 'application/json';
    }

    const token = this.token();
    if (token && !publica) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    return next.handle(req.clone({ setHeaders: headers })).pipe(
      catchError((err: HttpErrorResponse) => {
        if (err.status === 401 && !publica) {
          localStorage.removeItem(this.storageKey);
          this.router.navigateByUrl('/login', { replaceUrl: true });
        }
        return throwError(() => err);
      })
    );
  }

  private token(): string | null {
    try {
      const raw = localStorage.getItem(this.storageKey);
      return raw ? JSON.parse(raw)?.token ?? null : null;
    } catch {
      return null;
    }
  }
}