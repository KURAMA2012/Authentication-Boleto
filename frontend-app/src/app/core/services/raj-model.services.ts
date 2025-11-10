import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment.prod';
import { BSResponse } from '../domain/BSResponse';
import { BSAuth } from './BSAuth.service';
import { BSMessage } from './BSMessage.service';
import { BSAppService } from './BSApp.services';

@Injectable()
export class RAJResource {
  constructor(
    private http: HttpClient,
    private bsMessage: BSMessage,
    private bsAuth: BSAuth,
    private bsAppService:BSAppService
    ) {
  }

  urlScript: any;
  urlCamera: any;
  tipo_relatorio: any;
  titulo_cabecalho: any;
  logoFooterRight: any;

  getGrupoRelatorios() {
    const body = `{"Autenticacao":{"Login":"${this.bsAuth.usuario.login}","Senha":"${this.bsAuth.usuario['senha']}", "CodUsuar":"${this.bsAuth.usuario['codUsuario']}"},"ReqPLUG":{}}`;

    var headers = new HttpHeaders({
      'Access-Control-Allow-Origin': '*'
    })

    return this.http.post<BSResponse>(this.bsAppService.getUrlFull()  + 'ListarBotoes', body, { headers, withCredentials: false })
      .pipe(
        catchError(err => {
          this.bsMessage.errorOnRequest(err)
          return Promise.reject(err);
        })
      )
  }


  get_diretorListarTabs() {
    const body = `{"Autenticacao":{"Login":"${this.bsAuth.usuario.login}","Senha":"${this.bsAuth.usuario['senha']}", "CodUsuar":"${this.bsAuth.usuario['codUsuario']}"},"ReqPLUG":{}}`;

    var headers = new HttpHeaders({
      'Access-Control-Allow-Origin': '*'
    })

    return this.http.post<BSResponse>(this.bsAppService.getUrlFull()  + 'diretorListarBotoes', body, { headers, withCredentials: false })
      .pipe(
        catchError(err => {
          this.bsMessage.errorOnRequest(err)
          return Promise.reject(err);
        })
      )
  }

  
  processarHtml(script) {
    const body = `{"Autenticacao":{"Login":"${this.bsAuth.usuario.login}","Senha":"${this.bsAuth.usuario['senha']}", "CodUsuar":"${this.bsAuth.usuario['codUsuario']}"},"ReqPLUG":{"Script":"${script}"}}`;

    var headers = new HttpHeaders({
      'Access-Control-Allow-Origin': '*'
    })


    return this.http.post<BSResponse>(this.bsAppService.getUrlFull() + 'ProcessarHTML', body, { headers, withCredentials: false })
      .pipe(
        catchError(err => {
          this.bsMessage.errorOnRequest(err)
          return Promise.reject(err);
        })
      )
  }

  enviarRelatorioAnalise(script) {
    const body = `{"Autenticacao":{"Login":"${this.bsAuth.usuario.login}","Senha":"${this.bsAuth.usuario['senha']}", "CodUsuar":"${this.bsAuth.usuario['codUsuario']}"},"ReqPLUG":{"Script":"${script}"}}`;

    var headers = new HttpHeaders({
      'Access-Control-Allow-Origin': '*'
    })


    return this.http.post<BSResponse>(this.bsAppService.getUrlFull() + 'UltimaURL', body, { headers, withCredentials: false })
      .pipe(
        catchError(err => {
          this.bsMessage.errorOnRequest(err)
          return Promise.reject(err);
        })
      )
  }

  getRelatoriosByAgrupador(agrupador) {
    const body = `{"Autenticacao":{"Login":"${this.bsAuth.usuario.login}","Senha":"${this.bsAuth.usuario['senha']}", "CodUsuar":"${this.bsAuth.usuario['codUsuario']}"},"ReqPLUG":{"SubGrupo":"${agrupador}"}}`;

    var headers = new HttpHeaders({
      'Access-Control-Allow-Origin': '*'
    })


    return this.http.post<BSResponse>(this.bsAppService.getUrlFull() +'ListarBotoes', body, { headers, withCredentials: false })
      .pipe(
        catchError(err => {
          this.bsMessage.errorOnRequest(err)
          return Promise.reject(err);
        })
      )
  }

  connect(url, porta) {
    let login = "TESTE"
    let senha = btoa("TESTE")
    const body = `{"Autenticacao":{"NUsuario":"${login}","Senha":" ${senha}"}}`;

    var headers = new HttpHeaders({
      'Access-Control-Allow-Origin': '*'
    })

    return this.http.post<BSResponse>(url + ':' +  porta + '/datasnap/rest/app/autenticar', body, { headers, withCredentials: false })
      .pipe(
        catchError(err => {
          this.bsMessage.errorOnRequest(err)
          return Promise.reject(err);
        })
      )
  }

  ping(url, porta) {
    var headers = new HttpHeaders({
      'Access-Control-Allow-Origin': '*'
    })
    
    return this.http.post<BSResponse>(url + ':' +  porta + '/datasnap/rest/app/statusservico',  { headers, withCredentials: false })
      .pipe(
        catchError(err => {
          this.bsMessage.errorOnRequest(err)
          return Promise.reject(err);
        })
      )
  }

  LerCodBarraImpressaoUrl(codigoBarra) {
    const body = `{"Autenticacao":{"Login":"${this.bsAuth.usuario.login}","Senha":"${this.bsAuth.usuario['senha']}", "CodUsuar":${this.bsAuth.usuario['codUsuario']}},"ReqPLUG":{"codBarra":"${codigoBarra}"}}`;
    console.log(body)
    var headers = new HttpHeaders({
      'Access-Control-Allow-Origin': '*'
    })

    return this.http.post<BSResponse>(this.bsAppService.getUrlFull()  + 'terminalLerCodBarraImpressaoUrl', body, { headers, withCredentials: false })
      .pipe(
        catchError(err => {
          this.bsMessage.errorOnRequest(err)
          return Promise.reject(err);
        })
      )
  }


  terminalLogOff(dataLoguin) {
    const body = `{"Autenticacao":{"Login":"${this.bsAuth.usuario.login}","CodUsuar":${this.bsAuth.usuario['codUsuario']},"appVersao": "${this.bsAppService.version}","dataLoguin":"${dataLoguin}"}}`;
    console.log(body)
    var headers = new HttpHeaders({
      'Access-Control-Allow-Origin': '*'
    })

    let cUrl = this.bsAppService.getUrlFull()  + 'terminalLogOff'
    this.http.post<BSResponse>(cUrl, body, { headers, withCredentials: false }).pipe(
      catchError(err => {
          let Teste = err['message']

          return Promise.reject(err);
      })
    ).subscribe(response => {
      return Promise.resolve(response);
    })
  }

  dadosCards() {
    const body = `{"Autenticacao":{"Login":"${this.bsAuth.usuario.login}","Senha":"${this.bsAuth.usuario['senha']}", "CodUsuar":${this.bsAuth.usuario['codUsuario']}},"ReqPLUG":{}}`;
    console.log(body)
    var headers = new HttpHeaders({
      'Access-Control-Allow-Origin': '*'
    })

    return this.http.post<BSResponse>(this.bsAppService.getUrlFull()  + 'diretorHomeCards', body, { headers, withCredentials: false })
      .pipe(
        catchError(err => {
          this.bsMessage.errorOnRequest(err)
          return Promise.reject(err);
        })
      )
  }

  dadosGraficos() {
    const body = `{"Autenticacao":{"Login":"${this.bsAuth.usuario.login}","Senha":"${this.bsAuth.usuario['senha']}", "CodUsuar":${this.bsAuth.usuario['codUsuario']}},"ReqPLUG":{}}`;
    console.log(body)
    var headers = new HttpHeaders({
      'Access-Control-Allow-Origin': '*'
    })

    return this.http.post<BSResponse>(this.bsAppService.getUrlFull()  + 'diretorHomeGraficos', body, { headers, withCredentials: false })
      .pipe(
        catchError(err => {
          this.bsMessage.errorOnRequest(err)
          return Promise.reject(err);
        })
      )
  }

  logSistema(codHttp, msgErro, url) {
    const body = `{"Autenticacao":{"Login":"${this.bsAuth.usuario.login}","Senha":"${this.bsAuth.usuario['senha']}", "CodUsuar":${this.bsAuth.usuario['codUsuario']}},"ReqPLUG":{codigoHttp:${codHttp}, msgErro:"${msgErro}", url:"${url}"}}`;
    console.log(body)
    var headers = new HttpHeaders({
      'Access-Control-Allow-Origin': '*'
    })

    return this.http.post<BSResponse>(this.bsAppService.getUrlFull()  + 'logSistema', body, { headers, withCredentials: false })
      .pipe(
        catchError(err => {
          this.bsMessage.errorOnRequest(err)
          return Promise.reject(err);
        })
      )
  }
}