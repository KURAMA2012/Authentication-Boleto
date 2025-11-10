import { BSAuth } from './../../core/services/BSAuth.service';
import { ClienteResource } from './../../core/services/app-model.services';
import { Component, OnInit } from '@angular/core';
import { NavController } from '@ionic/angular';
import { BSMessage } from './../../core/services/BSMessage.service';
import { BSLoading } from 'src/app/core/services/BSLoading.service';
import { BSAppService } from 'src/app/core/services/BSApp.services';


@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.page.html',
  styleUrls: ['./edit-profile.page.scss'],
})
export class EditProfilePage implements OnInit {
  usuario:any = {}
  url = null
  porta = null
  habilitarAcessoDigital:Boolean = false

  constructor(
      private bsMessage:BSMessage,
      private bsAuth:BSAuth,
      private bsApp:BSAppService,
      public navCtrl: NavController,
    ){
      
      this.url  = this.bsApp.url
      this.porta = this.bsApp.porta

      this.habilitarAcessoDigital =  this.bsApp.isHabilitarAcessoDigital()
    }

  ngOnInit() {
    this.usuario = this.bsAuth.usuario
  }

  async sendData() {
    const self = this
    this.bsMessage.confirma('Deseja continuar com fluxo', ()=> {
      self.bsMessage.informa('registro salvo com sucesso');
    });
  }

  configurar(){
    this.navCtrl.navigateRoot('/configuracao')
  }

  setHabilitarAcessoDigital(){
    console.log(this.habilitarAcessoDigital)
    if (this.habilitarAcessoDigital == true){
      this.bsApp.setHabilitarAcessoDigital(this.bsAuth.usuario.login, this.bsAuth.usuario['senhaLogin'])
    }else {
      this.bsApp.removeHabilitarAcessoDigital()
    }
  }

  goToHome(){
    this.navCtrl.navigateForward('home-results')
  }
}
