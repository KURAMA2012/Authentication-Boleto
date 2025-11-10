import { SuporteChatComponent } from '../modal/suporte-chat';
import { Component, OnInit } from '@angular/core';
import { ChamadoResource } from 'src/app/core/services/app-model.services';
import { BSAuth } from './../../../core/services/BSAuth.service';
import { ModalController, NavController } from '@ionic/angular';

@Component({
  selector: 'app-suporte-chamados',
  templateUrl: './suporte-chamados.page.html',
  styleUrls: ['./suporte-chamados.page.scss'],
})
export class SuporteChamadosPage implements OnInit {

  loadChamadosAberto = false
  loadChamadosAtendimento = false
  loadChamadosEncerrado = false
  chamadosAbertos = []
  chamadosAtendimento = []
  chamadosEncerrados = []

  constructor(
    private chamadoResource: ChamadoResource,
    private bsAuth: BSAuth,
    private modalController: ModalController,
    public navCtrl: NavController, ) {

  }

  ngOnInit() {
    this.chamadoResource.getChamadosByLogin(this.bsAuth.usuario.login)
      .subscribe(
        data => {
          this.setChamadosAbertos(data)
          this.setChamadosAtendimento(data)
          this.setChamadosEncerrados(data)

          this.setOpenChamadosAberto()
        }
      )
  }

  setChamadosAbertos(chamados) {
    for (let chamado of chamados) {
      if (chamado.status == 'aberto') {
        this.chamadosAbertos.push(chamado)
      }
    }
  }

  setChamadosAtendimento(chamados) {
    for (let chamado of chamados) {
      if (chamado.status == 'atendimento') {
        this.chamadosAtendimento.push(chamado)
      }
    }
  }

  setChamadosEncerrados(chamados) {
    for (let chamado of chamados) {
      if (chamado.status == 'fechado') {
        this.chamadosEncerrados.push(chamado)
      }
    }
  }

  setOpenChamadosAberto() {
    this.loadChamadosAberto = true
    this.loadChamadosAtendimento = false
    this.loadChamadosEncerrado = false
  }

  setOpenChamadosAtendimento() {
    this.loadChamadosAberto = false
    this.loadChamadosAtendimento = true
    this.loadChamadosEncerrado = false
  }

  setOpenChamadosEncerrado() {
    this.loadChamadosAberto = false
    this.loadChamadosAtendimento = false
    this.loadChamadosEncerrado = true
  }

  async abrirChat(chamado) {
    const modal = await this.modalController.create({
      component: SuporteChatComponent,
      keyboardClose: true,
      componentProps: {
        'titulo': "Chat",
        'chamado':chamado
      }
    });
    return await modal.present();
  }
 
  /*
    this.navCtrl.navigateForward(['/suporte-chat', chamado.id]);
  }
  */
}
