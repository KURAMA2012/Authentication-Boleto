import { Component, ElementRef, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RAJResource } from 'src/app/core/services/raj-model.services';
import { DomSanitizer } from '@angular/platform-browser';
import { BSAnaliseRelatorios } from 'src/app/core/services/BSAnaliseRelatorios';
import { BSMessage } from 'src/app/core/services/BSMessage.service';
import { Platform } from '@ionic/angular';
import { Browser } from '@capacitor/browser';


@Component({
  selector: 'app-filtros',
  templateUrl: './filtros.page.html',
  styleUrls: ['./filtros.page.scss']
})
export class FiltrosPage {
  @ViewChild('frameFiltro') frameFiltro: ElementRef;
  urlFiltros = null
  titulo: any = null
  urlAnalise: any = null
  script:null

  options: any = {
    location: 'yes',//Or 'no' 
    hidden: 'no', //Or  'yes'
    clearcache: 'yes',
    clearsessioncache: 'yes',
    zoom: 'yes',//Android only ,shows browser zoom controls 
    hardwareback: 'yes',
    mediaPlaybackRequiresUserAction: 'no',
    shouldPauseOnSuspend: 'no', //Android only 
    closebuttoncaption: 'Close', //iOS only
    disallowoverscroll: 'no', //iOS only 
    toolbar: 'yes', //iOS only 
    enableViewportScale: 'no', //iOS only 
    allowInlineMediaPlayback: 'no',//iOS only 
    presentationstyle: 'pagesheet',//iOS only 
    fullscreen: 'yes',//Windows only    
  };

  constructor(
    private route: ActivatedRoute,
    private rajResource: RAJResource,
    private domSanitizer: DomSanitizer,
    private bsRelatorioAnalise: BSAnaliseRelatorios,
    private bsMessagem: BSMessage,
    public platform: Platform
  ) {
    this.route.params.subscribe(parametros => {
      console.log(parametros['script:'])
      if (parametros['script:']) {
        let jsonParam = JSON.parse(parametros['script:'])
        this.script = jsonParam['script']
        this.titulo = jsonParam['agrupador'] + ' / ' + jsonParam['caption']
        this.rajResource.processarHtml(this.script).subscribe(data => {
          let url = data['result'][0]['URL']
          //this.urlAnalise = data['result'][0]['URL']
          console.log('Codigo URL-->' + url)
          this.urlFiltros = this.domSanitizer.bypassSecurityTrustResourceUrl(url)
          //this.openWithCordovaBrowser(url)
        })
      }
    });
  }

  onLoadFunc() {
    /*
    console.log('GetIframe')
    var x = document.getElementsByTagName("iframe")[0].contentWindow;
    var doc:any = document.getElementsByTagName("iframe")[0]
    console.log(document['src'])
    console.log(x.document.getElementsByTagName("body")[0])
    */
  }
  enviarRelatorioAnalise() {
    console.log('Incluido relatorio analise:' + this.urlAnalise)
    this.rajResource.enviarRelatorioAnalise (this.script).subscribe(data => {
      if (data['result'][0]['Falhas'] != undefined &&data['result'][0]['Falhas'].length > 0){
        this.bsMessagem.error(data['result'][0]['Falhas'])
        return
      }
      
      let relatorioAnalise = {}
      relatorioAnalise['titulo'] = this.titulo
      relatorioAnalise['url'] = data['result'][0]['URL']
      this.bsRelatorioAnalise.addRelatorios(relatorioAnalise)
      this.bsMessagem.informa("Relatório enviado com sucesso")
    })
  }

  // Abre no navegador do sistema
  public async openWithSystemBrowser(url: string) {
    await Browser.open({
      url,
      windowName: '_system' // abre fora do app, navegador nativo
    });
  }

  // Abre "dentro do app" (abre uma nova janela nativa)
  public async openWithInAppBrowser(url: string) {
    await Browser.open({
      url,
      windowName: '_blank' // abre em nova janela
    });
  }

  // "_self" abre na mesma janela do app (não muito usado em Capacitor)
  public async openWithCordovaBrowser(url: string) {
    await Browser.open({
      url,
      windowName: '_self' // abre na mesma janela (comportamento limitado)
    });
  }
}