import { Component, OnInit } from '@angular/core';
import { RAJResource } from 'src/app/core/services/raj-model.services';
import { DomSanitizer } from '@angular/platform-browser';
import { NavController } from '@ionic/angular';
import { BSMessage } from './../../core/services/BSMessage.service';
import { BSLoading } from 'src/app/core/services/BSLoading.service';
import { Router } from '@angular/router';
import { Platform } from '@ionic/angular';
import { Browser } from '@capacitor/browser';

@Component({
  selector: 'app-relatorio',
  templateUrl: './relatorio.page.html',
  styleUrls: ['./relatorio.page.scss'],
})
export class RelatorioPage implements OnInit {

  urlScript: any;
  urlCamera: any;

  relatorio: boolean;
  codigo: boolean;
  preparandoRelatorio: boolean;

  tipo = this.rajResource.tipo_relatorio;
  url = this.rajResource.urlScript;
  titulo_cabecalho = this.rajResource.titulo_cabecalho;


  constructor(
    private rajResource: RAJResource,
    private domSanitizer: DomSanitizer,
    private navCtrl: NavController,
    private BSMessage: BSMessage,
    private BSLoading: BSLoading,
    private platform: Platform,
  ) { }

  ngOnInit() {

    this.relatorio = false;
    this.codigo = false;

    if (this.tipo == 'script') {
      this.preparandoRelatorio = true
      this.rajResource.processarHtml(this.url).subscribe(data => {
        let url = data['result'][0]['URL']
        this.urlScript = this.domSanitizer.bypassSecurityTrustResourceUrl(url)
        this.preparandoRelatorio = false
        this.relatorio = true;
      })
    } else if (this.tipo == 'camera') {
      try {
        this.rajResource.LerCodBarraImpressaoUrl(this.rajResource.urlCamera).subscribe(data => {
          this.BSLoading.unloading()
          let dataObject = data['result'][0]
          let url = dataObject['URL']
          this.urlCamera = this.domSanitizer.bypassSecurityTrustResourceUrl(url)
          this.preparandoRelatorio = false
          this.codigo = true;
          let msg = dataObject['Falhas']
          if (msg != null && msg != undefined && msg.length > 0) {
            this.BSMessage.informa(msg)
          }
        })
      } catch (error) {
        this.BSLoading.unloading()
        this.BSMessage.error(error)
      }
    }
  }

  goToHome() {
    this.navCtrl.navigateForward('home-results');
    this.rajResource.titulo_cabecalho = "";
  }

}
