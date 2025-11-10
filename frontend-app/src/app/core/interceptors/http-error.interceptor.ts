import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError, from } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { BSMessage } from '../services/BSMessage.service';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {

  constructor(private bsMessage: BSMessage) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        this.handleErrorAsync(error);


        return throwError(() => error);
      })
    );
  }

  private async handleErrorAsync(error: HttpErrorResponse): Promise<void> {
    const backendMessage = error?.error?.message;
    if (backendMessage) {
      await this.bsMessage.error(backendMessage);
      return;
    }

    const statusCode = error.status;
    if (statusCode === 404) {
      await this.bsMessage.alerta('Recurso não encontrado.');
    } else if (statusCode === 409) {
      await this.bsMessage.error('Conflito de dados detectado.');
    } else if (statusCode === 500) {
      await this.bsMessage.error('Erro interno no servidor.');
    } else {
      await this.bsMessage.error('Erro inesperado. Tente novamente.');
    }
  }
}
