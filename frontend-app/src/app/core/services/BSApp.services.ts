import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable, OnInit } from "@angular/core";
import { catchError } from "rxjs/operators";
import { BSResponse } from "../domain/BSResponse";
import { BSLoading } from "./BSLoading.service";
import { BSMessage } from "./BSMessage.service";

enum eStatusServidor {
    naoTestado,   // CINZA
    serverConfigurado, // AMARELO
    serverTestando, // AMARELO
    serverAgRetorno, // AMARELO
    serverConectado, // VERDE
    serverFalhaConexao, // VERMELHO
    serverNaoConfigurado // VERMELHO
}

enum eConexaoServidor{
    conexaoAguardando, 
    conexaoTestando, 
    conexaoSucesso, 
    conexaoFalha
}

@Injectable()
export class BSAppService implements OnInit {
   

    url: any = null
    porta: any = null

    url2: any = null
    porta2: any = null


    private logConexaoSrv1: string = ""
    private logConexaoSrv2: string = ""
    private pararTesteSrv1 = true;
    private pararTesteSrv2 = true;
    private statusServico: eStatusServidor = eStatusServidor.naoTestado;
    private eStatusConexaoSrv1: eConexaoServidor = eConexaoServidor.conexaoAguardando;
    private eStatusConexaoSrv2: eConexaoServidor = eConexaoServidor.conexaoAguardando;

    configurado: boolean = false
    lembrarUsuario: boolean = false
    usuarioLebrar: string = null
    version = "3.0.0"
    habiltarAcessoDigital = false;

    constructor(private http: HttpClient,
        private bsMessage: BSMessage,
        private bsLoading:BSLoading
    ) {
        this.loadConfiguracao()
    }
    ngOnInit(): void {
        console.log('start-config')
    }

    getTokenDevice() {
        return localStorage.getItem('tokenDevice')
    }
    
    LogConexaoDetalhado() {/*App-Diretor Método que apresenta os logs de conexao aos Srv1 e Srv2.*/
        return "Log SRV 1:"+ this.logConexaoSrv1 +"\n \n"+ "Log SRV 2:"+ this.logConexaoSrv2
    }


    LerConfiguracao() {/*App-Diretor Método que lerConfiguração.*/
        if (this.statusServico == eStatusServidor.naoTestado) {
            
            let url = this.getConfigServer1Url()
            let porta = this.getConfigServer1Porta()

            let url2 = this.getConfigServer2Url()
            let porta2 = this.getConfigServer2Porta()

            if (((url != "") && (porta != "")) || ((url2 != "") && (porta2 != "")))
                this.statusServico = eStatusServidor.serverConfigurado
            else
                this.statusServico = eStatusServidor.serverNaoConfigurado
        }
    }

    //2023_
    SalvarConfiguracao(url1, porta1, url2, porta2) {/*App-Diretor*/
        this.url = url1
        this.porta = porta1
        this.url2 = url2
        this.porta2 = porta2
        // Salvar Variaveis
        this.setConfiguracaoStorage1(url1, porta1)
        this.setConfiguracaoStorage2(url2, porta2)
        this.statusServico = eStatusServidor.naoTestado;
    }

    ForcarNovoTesteDeConexao(){
        this.statusServico = eStatusServidor.naoTestado;
    }

    TestarConexao() {/*App-Diretor*/
        if (this.statusServico == eStatusServidor.serverAgRetorno) {
            
            if (this.eStatusConexaoSrv1 == eConexaoServidor.conexaoSucesso){
                this.statusServico = eStatusServidor.serverConectado
                
                this.url = this.getConfigServer1Url()
                this.porta = this.getConfigServer1Porta()
                this.setConfiguracaoStorage(this.url, this.porta) 
            }
            else if (this.eStatusConexaoSrv2 == eConexaoServidor.conexaoSucesso){
                this.statusServico = eStatusServidor.serverConectado

                this.url = this.getConfigServer2Url()
                this.porta = this.getConfigServer2Porta()
                this.setConfiguracaoStorage(this.url, this.porta) 
            }
            else if (this.eStatusConexaoSrv1 == eConexaoServidor.conexaoTestando)
                {}
            else if (this.eStatusConexaoSrv2 == eConexaoServidor.conexaoTestando)
                {}
            else if ((this.eStatusConexaoSrv1 == eConexaoServidor.conexaoFalha ) && (this.eStatusConexaoSrv2 == eConexaoServidor.conexaoFalha))
                { this.statusServico = eStatusServidor.serverFalhaConexao }

            else if ((this.eStatusConexaoSrv1 == eConexaoServidor.conexaoAguardando ) && (this.eStatusConexaoSrv2 == eConexaoServidor.conexaoFalha))
                { this.statusServico = eStatusServidor.serverFalhaConexao }

            else if ((this.eStatusConexaoSrv1 == eConexaoServidor.conexaoFalha ) && (this.eStatusConexaoSrv2 == eConexaoServidor.conexaoAguardando))
                { this.statusServico = eStatusServidor.serverFalhaConexao }
            else
                {}
        }       
        else if (this.statusServico == eStatusServidor.serverConfigurado) {
            this.statusServico = eStatusServidor.serverTestando
            this.logConexaoSrv1 = ""
            this.logConexaoSrv2 = ""
            this.AssincronoRodaStatusServico()
        }
    }

    private SicronoTestaServicoEstaOperando(nUrl, nPorta, nEhSrv1): string {
        console.log("AssincTestaServicoEstaOperando  nEhSrv1 "+ nEhSrv1)
        let cLogConexao = "testando"
        var headers = new HttpHeaders({
            'Access-Control-Allow-Origin': '*'
        })
        
        if ((nUrl == "") || (nPorta == ""))
        {
            cLogConexao = "URL '" + nUrl + "' ou Porta '" + nPorta + "' não informado"
            if ( nEhSrv1 == true )
                this.eStatusConexaoSrv1 = eConexaoServidor.conexaoAguardando;
            else
                this.eStatusConexaoSrv2 = eConexaoServidor.conexaoAguardando;
        }    
        else {
            if ( nEhSrv1 == true )
                this.eStatusConexaoSrv1= eConexaoServidor.conexaoTestando;
            else
                this.eStatusConexaoSrv2= eConexaoServidor.conexaoTestando;
            var urlApp = '/datasnap/rest/app/statusServico'
            this.http.get<BSResponse>(nUrl + ':' + nPorta + urlApp).pipe(                
                catchError(err => {
                    console.warn('statusServico Pipe')    
                        cLogConexao = err['message']
                        if ( nEhSrv1 == true ){
                            this.logConexaoSrv1 = this.logConexaoSrv1 +"\n Erro no Srv1:\n "+ cLogConexao
                            this.pararTesteSrv1 = true
                        } else {
                            this.logConexaoSrv2 = this.logConexaoSrv2 +"\n Erro no Srv2:\n "+ cLogConexao;
                            this.pararTesteSrv2 = true
                        }

                        if ( nEhSrv1 == true )
                            this.eStatusConexaoSrv1 = eConexaoServidor.conexaoFalha;
                        else
                            this.eStatusConexaoSrv2 = eConexaoServidor.conexaoFalha;

                        return Promise.reject(err);
                    })
                )
                .subscribe(response => {
                    cLogConexao = "";
                    
                    if ( nEhSrv1 == true )
                        this.eStatusConexaoSrv1 = eConexaoServidor.conexaoSucesso;
                    else
                        this.eStatusConexaoSrv2 = eConexaoServidor.conexaoSucesso;

                    if ( nEhSrv1 == true ){
                        this.pararTesteSrv1 = true
                    }else{
                        this.pararTesteSrv2 = true;
                    }
                    // Preciso ter o Return dentro do consumo ?
                    //return Promise.resolve(response);
                    // Preciso ter o Return dentro do consumo ?
                    if ( nEhSrv1 == true ){
                        this.pararTesteSrv1 = true
                    }else{
                        this.pararTesteSrv2 = true;
                    }
                     console.warn('statusServico Promise Resolve')
                     return Promise.resolve(response);
                })
        }

        if ( nEhSrv1 == true ){
            this.pararTesteSrv1 = true
        }else{
            this.pararTesteSrv2 = true;
        }
        
        return cLogConexao;
    }

    private AssincronoRodaStatusServico() {
        
        if (this.statusServico == eStatusServidor.serverTestando) {

            this.pararTesteSrv1 = false
            var cUrl = this.getConfigServer1Url()
            var cPorta = this.getConfigServer1Porta()
            this.logConexaoSrv1 = this.logConexaoSrv1+ "\n Inicio Teste Conexao srv1 - " + cUrl + ":" + cPorta;
            
            var cTeste = this.SicronoTestaServicoEstaOperando(cUrl, cPorta, true);
            
            this.logConexaoSrv1 = this.logConexaoSrv1+ "\n" + cTeste
                 
            if (cTeste == "") {
                this.statusServico = eStatusServidor.serverConectado
            } else {
                this.pararTesteSrv2 = false
                var cUrl = this.getConfigServer2Url()
                var cPorta = this.getConfigServer2Porta()
                this.logConexaoSrv2 = this.logConexaoSrv2 +"\n Inicio Teste Conexao srv2 - " + cUrl + ":" + cPorta;
                cTeste = this.SicronoTestaServicoEstaOperando(cUrl, cPorta, false)
                this.logConexaoSrv2 = this.logConexaoSrv2 +"\n" + cTeste

            }

            this.statusServico = eStatusServidor.serverAgRetorno
        }
    }

    EstaConectado(): Boolean {
        if (this.statusServico == eStatusServidor.serverConectado) {
            return true
        } else {
            return false
        }
    }
        
    EstaComFalhaDeConexaoEmAmbosOsServers(): Boolean {
        if (this.statusServico == eStatusServidor.serverFalhaConexao) {
            return true
        } else {
            return false
        }
    }

    isServerConfigurado(): boolean {
        return this.isServer1Configurado() || this.isServer2Configurado()
    }

    getStatusServerInteger(): number {
        //naoTestado,   // CINZA
        //serverConfigurado, // AMARELO
        //serverTestando, // AMARELO
        //serverConectado, // VERDE
        //serverFalhaConexao, // VERMELHO
        //serverNaoConfigurado // VERMELHO

        switch (this.statusServico) {
            case eStatusServidor.serverConfigurado: return 2;
            case eStatusServidor.serverTestando: return 2;
            case eStatusServidor.serverAgRetorno: return 2;
            case eStatusServidor.serverConectado: return 1;
            case eStatusServidor.serverFalhaConexao: return 4;
            case eStatusServidor.serverNaoConfigurado: return 3;
            default: //case eStatusServidor.naoTestado: return 3;
                return 5
        }
    }

    StatusServerNome(): string {
        switch (this.statusServico) {
            case eStatusServidor.serverConfigurado: return "serverConfigurado";
            case eStatusServidor.serverTestando: return "serverTestando";
            case eStatusServidor.serverAgRetorno: return "serverAgRetorno";
            case eStatusServidor.serverConectado: return "serverConectado";
            case eStatusServidor.serverFalhaConexao: return "serverFalhaConexao";
            case eStatusServidor.serverNaoConfigurado: return "serverNaoConfigurado";
            default: //case eStatusServidor.naoTestado: return 3;
                return "naoTestado"
        }
    }

    StatusServerCor(): string {
        switch (this.statusServico) {
            case eStatusServidor.serverConfigurado: return "amarelo";
            case eStatusServidor.serverTestando: return "amarelo";
            case eStatusServidor.serverAgRetorno: return "amarelo";
            case eStatusServidor.serverConectado: return "verde";
            case eStatusServidor.serverFalhaConexao: return "vermelho";
            case eStatusServidor.serverNaoConfigurado: return "vermelho";
            default: //case eStatusServidor.naoTestado: return 3;
                return "cinza"
        }
    }



    /*
    configConnection() {
        console.log('configConnection')
        //this.testConnection()

        /*
        setTimeout(()=> {
            let isServer1Conntected: boolean = this.isServer1Conntected()
            let isServer2Conntected: boolean = this.isServer2Conntected()
    
            if (isServer1Conntected == true) {
                this.setConfiguracaoStorage(this.getConfigServer1Url(), this.getConfigServer1Porta())
            } else {
                if (isServer2Conntected == true) {
                    this.setConfiguracaoStorage(this.getConfigServer2Url(), this.getConfigServer2Porta())
                } 
            }
            this.loadConfiguracao()
        }, 4000)
        
    }

    testConnection() {
        console.log('test-connection')
        let login = "TESTE"
        let senha = btoa("TESTE")
        //const body = `{"Autenticacao":{"NUsuario":"${login}","Senha":" ${senha}"}}`;

        var headers = new HttpHeaders({
            'Access-Control-Allow-Origin': '*'
        })

        var urlApp = '/datasnap/rest/app/statusservico'

        if (this.isServer1Configurado()) {
            let urlServer1 = this.getConfigServer1Url()
            let portaServer1 = this.getConfigServer1Porta()
            this.http.get<BSResponse>(urlServer1 + ':' + portaServer1 + urlApp)
            .pipe(
                catchError(err => {
                  this.setServer1Conntected(false)

                  if (this.isServer2Configurado()) {
                    let urlServer2 = this.getConfigServer2Url()
                    let portaServer2 = this.getConfigServer2Porta()
                    this.http.get<BSResponse>(urlServer2 + ':' + portaServer2 + urlApp)
                    .pipe(
                        catchError(err => {
                          this.setServer2Conntected(false)
                          return Promise.reject(err);
                        })
                      )
                    .subscribe(response => {
                        this.setServer2Conntected(true)
                        this.setConfiguracaoStorage(this.getConfigServer2Url(), this.getConfigServer2Porta())
                    })
                }
                
                  return Promise.reject(err);
                })
              )
            .subscribe(response => {
                this.setServer1Conntected(true)
                this.setConfiguracaoStorage(this.getConfigServer1Url(), this.getConfigServer1Porta())
            })
        } else {
            if (this.isServer2Configurado()) {
                let urlServer2 = this.getConfigServer2Url()
                let portaServer2 = this.getConfigServer2Porta()
                this.http.get<BSResponse>(urlServer2 + ':' + portaServer2 + urlApp)
                .pipe(
                    catchError(err => {
                      this.setServer2Conntected(false)
                      return Promise.reject(err);
                    })
                  )
                .subscribe(response => {
                    this.setServer2Conntected(true)
                    this.setConfiguracaoStorage(this.getConfigServer2Url(), this.getConfigServer2Porta())
                })
            }
        }
    }
    */

    loadConfiguracao() {
        this.url = localStorage.getItem('url')
        this.porta = localStorage.getItem('porta')

        let config = localStorage.getItem('configurado')
        if (config != null && config == "true") {
            this.configurado = true
        } else {
            this.configurado = false
        }
    }

    isConfigurado() {
        this.loadConfiguracao()
        return this.configurado
    }

    isConfiguracao() {
        this.loadConfiguracao();
    }

    setConfiguracaoStorage(url, porta) {
        localStorage.setItem('url', url)
        localStorage.setItem('porta', porta)
        localStorage.setItem('configurado', "true")
    }

    removeConfiguracaoStorage() {
        localStorage.setItem('url', null)
        localStorage.setItem('porta', null)
        localStorage.setItem('configurado', "false")
    }

    private setConfiguracaoStorage1(url:string, porta:string) {
        localStorage.setItem('urlServer1', url)
        localStorage.setItem('portaServer1', porta)
        
        // localStorage.setItem('configuradoServer1', "true")
        // localStorage.setItem('connectedServer1', "false")

        // if (url == "" || url.length == 0 || porta.length == 0) {
        //     localStorage.setItem('configuradoServer1', "false")
        // }
    }

    private setConfiguracaoStorage2(url:string, porta:string) {

        localStorage.setItem('urlServer2', url)
        localStorage.setItem('portaServer2', porta)
        // localStorage.setItem('configuradoServer2', "true")
        // localStorage.setItem('connectedServer2', "false")

        // if (url == "" || url.length == 0 || porta.length == 0) {
        //     localStorage.setItem('configuradoServer2', "false")
        // }
    }

    setServer1Conntected(value) {
        localStorage.setItem('connectedServer1', value)
    }

    isServer1Conntected(): boolean {
        let value = localStorage.getItem('connectedServer1')
        if (value === "true") {
            return true
        } else {
            return false
        }
    }

    isServer1Configurado(): boolean {
        let value = localStorage.getItem('configuradoServer1')
        if (value === "true") {
            return true
        } else {
            return false
        }
    }

    getConfigServer1Url() {
        return localStorage.getItem('urlServer1')
    }

    getConfigServer1Porta() {
        return localStorage.getItem('portaServer1')
    }

    isServer2Configurado(): boolean {
        let value = localStorage.getItem('configuradoServer2')
        if (value === "true") {
            return true
        } else {
            return false
        }
    }

    getConfigServer2Url() {
        return localStorage.getItem('urlServer2')
    }

    getConfigServer2Porta() {
        return localStorage.getItem('portaServer2')
    }

    isServer2Conntected(): boolean {
        let value = localStorage.getItem('connectedServer2')
        if (value === "true") {
            return true
        } else {
            return false
        }
    }

    setServer2Conntected(value) {
        localStorage.setItem('connectedServer2', value)
    }

    setLembrarUsuario(value: string, usuario: string, senha: string) {
        localStorage.setItem('lembrarUsuario', value)
        localStorage.setItem('usuarioLembrar', usuario)
        localStorage.setItem('senhaLembrar', senha)
    }

    isLembrarUsuario(): boolean {
        let value = localStorage.getItem('lembrarUsuario')
        if (value === "true") {
            return true
        } else {
            return false
        }
    }

    getUsuarioLembrar() {
        return localStorage.getItem('usuarioLembrar')
    }

    getSenhaLembrar() {
        return localStorage.getItem('senhaLembrar')
    }
    setHabilitarAcessoDigital(usuario, senha) {
        localStorage.setItem('habiltarAcessoDigital', "true")
        localStorage.setItem('habiltarAcessoDigitalUsuario', usuario)
        localStorage.setItem('habiltarAcessoDigitalSenha', senha)
    }

    getUsuarioAcessoDigital() {
        return localStorage.getItem('habiltarAcessoDigitalUsuario')
    }

    getSenhaAcessoDigital() {
        return localStorage.getItem('habiltarAcessoDigitalSenha')
    }


    removeHabilitarAcessoDigital() {
        localStorage.setItem('habiltarAcessoDigital', "false")
    }

    isHabilitarAcessoDigitalConfigurado(): boolean {
        let value = localStorage.getItem('habiltarAcessoDigital')
        if (value === "true" || value === "false") {
            return true
        } else {
            return false
        }
    }

    isHabilitarAcessoDigital(): boolean {
        let value = localStorage.getItem('habiltarAcessoDigital')
        if (value === "true") {
            return true
        } else {
            return false
        }
    }

    getUrlFull() {
        this.url = localStorage.getItem('url')
        this.porta = localStorage.getItem('porta')
        return this.url + ':' + this.porta + '/datasnap/rest/app/'
    }
}
