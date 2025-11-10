import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, finalize } from 'rxjs/operators';
import { BSLoading } from 'src/app/core/services/BSLoading.service';
import { BSResponse } from '../domain/BSResponse';
import { environment } from './../../../environments/environment.prod';
import { BSMessage } from './BSMessage.service';


@Injectable()
export class BSResource {
  path: String;
  response;

  constructor(
    private http: HttpClient,
    private bsMessage: BSMessage,
    private bsLoading:BSLoading
    ) {
  }

  setPath(path: String) {
    this.path = environment.apiUrl +  path ;
    console.log('Request api in :' + environment.apiUrl )
  }

  getBLOB(method) {
    return this.http.get(this.path + method, {
      headers: new HttpHeaders({
      }), responseType: 'blob'
    }).pipe(
      catchError(err => {
        this.bsMessage.errorOnRequest(err)
        return Promise.reject(err);
      })
    );
  }


  get(method, data?: any) {
    this.bsLoading.loading()
    if (data != undefined) {
      return this.http.get<BSResponse>(this.path + method + '?' + data).pipe(
        catchError(err => {
          this.bsMessage.errorOnRequest(err)
          return Promise.reject(err);
        }),
        finalize(() => {
          this.bsLoading.unloading()
        })
      );
    } else {
      return this.http.get<BSResponse>(this.path + method).pipe(
        catchError(err => {
          this.bsMessage.errorOnRequest(err)
          return Promise.reject(err);
        }),
        finalize(() => {
          this.bsLoading.unloading()
        })
      );
    }
  }

  post(method, data) {
    return this.http.post<BSResponse>(this.path + method, data).pipe(
      catchError(err => {
        this.bsMessage.errorOnRequest(err)
        return Promise.reject(err);
      })
    );
  }
  delete(method) {
    return this.http.delete<BSResponse>(this.path + method).pipe(
      catchError(err => {
        this.bsMessage.errorOnRequest(err)
        return Promise.reject(err);
      })
    );
  }

  put(method, data) {
    return this.http.put<BSResponse>(this.path + method, data).pipe(
      catchError(err => {
        this.bsMessage.errorOnRequest(err)
        return Promise.reject(err);
      })
    );
  }
}
