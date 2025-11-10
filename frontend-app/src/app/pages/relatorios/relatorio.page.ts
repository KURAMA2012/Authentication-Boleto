import { Component, OnInit } from '@angular/core';
import { BSAuth } from '../../core/services/BSAuth.service';
import { Pages } from '../../interfaces/pages';
import { ActivatedRoute } from '@angular/router';
import { RAJResource } from 'src/app/core/services/raj-model.services';
import { BSLoading } from 'src/app/core/services/BSLoading.service';
import { AppComponent } from 'src/app/app.component';
import { DomSanitizer } from '@angular/platform-browser';

import { BSMessage } from 'src/app/core/services/BSMessage.service';
import { catchError } from 'rxjs/operators';
import { NavController } from '@ionic/angular';



@Component({
  selector: 'app-relatorio',
  templateUrl: './relatorio.page.html',
  styleUrls: ['./relatorio.page.scss'],
})
export class RelatorioPage implements OnInit {
  titulo = null
  public appPages: Array<Pages> = [];
  urlRelatorio = ''
  urlScript = null
  urlCamera = null
  relatorioNotFound = ''
  tituloSubMenu = ''
  usuarioAutenticado = { nome: '', plano: '', login: '' } 

  preparandoRelatorio:boolean

  constructor(
    private route: ActivatedRoute,
    private rajResource:RAJResource,
    private bsLoading:BSLoading,
    private appComponent: AppComponent,
    private domSanitizer: DomSanitizer,
    private bsMessage: BSMessage,
    private bsAuth: BSAuth,
    public navCtrl: NavController,

  ) {

    // this.bsLoading.loading()
    // this.route.params.subscribe(parametros => {
    //   this.titulo = JSON.parse(parametros['agrupador'])['caption']
    //   let agrupador = JSON.parse(parametros['agrupador'])['agrupador']
    //   this.rajResource.getRelatoriosByAgrupador(agrupador).subscribe(data => {
    //     let response = data['result'][0]['Botoes']
    //     this.appPages = []
    //     for (let item of response){
    //       this.appPages.push(
    //         {
    //           title: item['Caption'],
    //           url:  '/filtros/' + `{"script":"${String(item['Script'])}","agrupador":" ${  agrupador }","caption":" ${  item['Caption']}"}`   ,
    //           direct: 'root',
    //           icon: 'albums-outline'
    //         },      
    //       )
    //     }

    //     this.bsLoading.unloading()
    //   })
    // });

    // this.appComponent.desativarFooter = true
  }
  ngOnInit() {
    
    this.preparandoRelatorio = true

    //this.urlRelatorio = this.appComponent.urlScript
    // this.titulo = this.appComponent.titulo

    this.preparandoRelatorio = false
    // this.tituloSubMenu = this.appComponent.tituloSubMenu
    this.verificaUrl()
    this.usuarioAutenticado = this.bsAuth.usuario
  }

  verificaUrl(){
    
    // if(this.appComponent.urlCamera != ''){
    //   this.tituloSubMenu = ''
    //   this.urlScript = this.appComponent.urlCamera
    // }
    

    // if(this.appComponent.urlScript != ''){
    //   this.urlRelatorio = this.appComponent.urlScript
    //   this.processaHTML(this.urlRelatorio)
    // }
  }

  processaHTML(script){
    this.preparandoRelatorio = true
    try {
      this.rajResource.processarHtml(script).subscribe(data => {
        
        let url = data['result'][0]['URL']
        let urlBase = data['result'][0]['BaseScp']

        if(urlBase != ""){
          this.urlScript = this.domSanitizer.bypassSecurityTrustResourceUrl(url)
          this.preparandoRelatorio = false
           
        }else{
          this.relatorioNotFound = 'notFound'
          this.tituloSubMenu = ''
          this.preparandoRelatorio = false
        }
        let falha = data['Falhas']
        
      })
    } catch (error) {
      
      console.error(error)
      this.bsMessage.informa(error)
    }


  }


  goToHome() {
    // this.appComponent.desativarFooter = false
    this.navCtrl.navigateForward('home-results');
  }

}
