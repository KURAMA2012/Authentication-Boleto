import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BSAuth } from './BSAuth.service';
import { BSMessage } from './BSMessage.service';
import { timeout } from 'rxjs/operators';


@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private bsAuth:BSAuth,private bsMessage:BSMessage){

  }

 intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
  console.log('Interceptando requisição para:', req.url);

  // Se for upload (FormData), não modificar headers
  if (req.url.includes('/upload')) {
    console.log('➡️ Requisição de upload detectada — sem alterar headers');
    return next.handle(req);
  }

  let timeoutRequest = 120000;
  const isUrlToken = req.url.includes('/autenticar');
  const isProcessarHtml = req.url.includes('/ProcessarHTML');

  if (isProcessarHtml) {
    timeoutRequest = 120000;
  }

  // ✅ Só define JSON quando for autenticação ou API normal
  if (isUrlToken) {
    req = req.clone({
      setHeaders: {
        'Content-Type': 'application/json; charset=utf-8',
        'Accept': 'application/json',
        'Access-Control-Allow-Origin': '*'
      },
    });
  } else {
    req = req.clone({
      setHeaders: {
        'Content-Type': 'application/json; charset=utf-8',
        'Accept': 'application/json',
        'Access-Control-Allow-Origin': '*'
      },
    });
  }

  return next.handle(req).pipe(timeout(timeoutRequest));
}
}