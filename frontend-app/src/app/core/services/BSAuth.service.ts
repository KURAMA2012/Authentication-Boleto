import { HttpClient, HttpHeaders } from '@angular/common/http';
import { EventEmitter, Injectable } from '@angular/core';
import { NavController } from '@ionic/angular';
import { catchError, map, finalize, timeout  } from 'rxjs/operators';
import { BSResponse } from '../domain/BSResponse';
import { environment } from './../../../environments/environment.prod';
import { BSMessage } from './BSMessage.service';
import { BSAppService } from './BSApp.services';
import { BSLoading } from './BSLoading.service';
import { Observable, observable } from 'rxjs';

@Injectable()
export class BSAuth {
    public usuario = { nome: '', plano: '', login: '', autenticado: false }
    //public usuario: any

    publishAutenticacao = new EventEmitter();

    constructor(
        private http: HttpClient,
        private bsMessage: BSMessage,
        public navCtrl: NavController,
        private bsRajApp:BSAppService,
        private bsLoading:BSLoading
    ) {
    }

    autentidarRAJ(login: string, senha: string, lembrarUsuario) {
        /*Prepare body */
        const senhaLembrar = senha.toUpperCase()
        login = login.toUpperCase()
        senha = senha.toUpperCase()
        
        senha = btoa(senha)
        const url = this.bsRajApp.getUrlFull() + 'autenticar'
        const body = `{"Autenticacao":{"NUsuario":"${login}","Senha":" ${senha}","appVersao": "${this.bsRajApp.version}"}}`;
        //const body = { "Autenticacao": { "NUsuario": "RAT", "Senha": "UkFU" } }

        console.log(url)
        var headers = new HttpHeaders({
            'Access-Control-Allow-Origin': 'http://localhost:8100'
        })

        //this.bsLoading.loading()
        this.http.post<BSResponse>(url, body, { headers, withCredentials: false})
        .pipe(
            catchError(err => {
                this.bsLoading.unloading()
                this.bsMessage.errorOnRequest(err)
                return Promise.resolve(err);
            })
        ).subscribe(response => {
            if (response['result'][0]['Falhas'].length >0 ){
                this.bsLoading.unloading()
                let error = response['result'][0]['Falhas'] 
                this.bsMessage.error(error)
                return Promise.reject(null);
            }

            //Promise.resolve(response);
            let userResponse =  response['result'][0]

            if(userResponse['CodUsuario'] == 0){
                this.bsLoading.unloading()
                this.bsMessage.error("Falha na Autenticação do usuário")
                return false
            }

            this.usuario['login'] = userResponse['Usuario']
            this.usuario['senhaLogin'] = senhaLembrar
            this.usuario['senha'] = userResponse['Senha']
            this.usuario['codUsuario'] = userResponse['CodUsuario']
            this.usuario['ativo'] = true
            this.usuario['nome'] = userResponse['Usuario']
            this.usuario['autenticado'] = true
            this.usuario['dataLoguin'] =  userResponse['dataLoguin']
            
            this.bsRajApp.setLembrarUsuario(lembrarUsuario,this.usuario['login'], senhaLembrar)
            
            this.redirecionarUsuario(this.usuario)
            this.publicarLoginLogoutEvent(this.usuario)
            this.bsLoading.unloading() 

                /*
                this.loadInfoToken(this.usuario, response)
            this.gravarTokenLocalStorage(response)

            this.usuario['autenticado'] = true
            this.redirecionarUsuario(this.usuario)
            this.publicarLoginLogoutEvent(this.usuario)
            */
        });
    }

    autenticar(login: string, senha: string) {
        /*
        login = 'admin'
        */
        senha = '123456'
        /*Prepare url token */
        const urlOauthToke = environment.apiUrl + 'oauth/token'

        //console.log('url:' + urlOauthToke)

        /*Prepare header */

        var headers = new HttpHeaders({
            'Authorization': 'Basic Y2xpZW50YXBwOjEyMzQ1Ng==',
            'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8',
        })


        /*Prepare body */
        const body = `username=${login}&password=${senha}&grant_type=password&scope=write`;
        //console.log('body:' + body)

        this.http.post<BSResponse>(urlOauthToke, body, { headers, withCredentials: true })
            .pipe(
                catchError(err => {
                    this.bsMessage.errorOnRequest(err)
                    return Promise.reject(err);
                })
            ).subscribe(response => {
                //  console.log(response)
                this.loadInfoToken(this.usuario, response)

                /*Grava o token no localstorage */
                this.gravarTokenLocalStorage(response)

                this.usuario['autenticado'] = true
                this.redirecionarUsuario(this.usuario)
                this.publicarLoginLogoutEvent(this.usuario)
            });
    }

    logout() {
        this.usuario['autenticado'] = false
        this.publicarLoginLogoutEvent(this.usuario)
        this.limparTokenLocalStorage()
        this.navCtrl.navigateRoot('/login');
    }
    redirecionarUsuario(usuario) {
        if (usuario['ativo']) {
            this.navCtrl.navigateRoot('home-results');
        } else {
            this.navCtrl.navigateForward('login');
        }
    }

    publicarLoginLogoutEvent(usuario) {
        this.publishAutenticacao.emit(usuario)
    }

    loadInfoToken(usuario, token) {
        //let infoToken = this.jwtHelper.decodeToken(token['access_token']);
        let infoToken = token['access_token']
        usuario['login'] = infoToken.loginMkAuth
        usuario['perfis'] = infoToken.authorities
        usuario['ativo'] = infoToken.ativo
        usuario['nome'] = infoToken.nome
        usuario['loginOAuth'] = infoToken.login
    }

    gravarTokenLocalStorage(token) {
        localStorage.setItem('access_token', token['access_token'])
        localStorage.setItem('refresh_token', token['refresh_token'])
    }

    limparTokenLocalStorage() {
        localStorage.setItem('access_token', null)
        localStorage.setItem('refresh_token', null)
    }

    refreshToken() {
        const urlOauthToke = environment.apiUrl + '/api/oauth/token'

        var headers = new HttpHeaders({
            'Authorization': 'Basic Y2xpZW50YXBwOjEyMzQ1Ng==',
            'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8'
        })

        const refresh_token = localStorage.getItem('refresh_token');
        const body = `grant_type=refresh_token&refresh_token=${refresh_token}`;

        return this.http.post<BSResponse>(urlOauthToke, body, { headers, withCredentials: true })
            .pipe(
                catchError(err => {
                    this.bsMessage.errorOnRequest(err)
                    return Promise.reject(err);
                }),
                map((response: BSResponse) => {
                    this.gravarTokenLocalStorage(response)
                    return Promise.resolve(response)
                })
            );
    }

    isTokenExpired() {
        let access_token = localStorage.getItem('access_token')
        if (!access_token) {
            return true
        }

//        let isTokenExpired = this.jwtHelper.isTokenExpired(access_token)
        let isTokenExpired = access_token
        return isTokenExpired
    }
}
