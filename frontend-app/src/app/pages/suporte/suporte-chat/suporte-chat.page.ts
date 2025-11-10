import { Component, Input, OnInit, ViewChild, ElementRef } from "@angular/core";
import { ModalController } from "@ionic/angular";
import { ChamadoResource } from './../../../core/services/app-model.services';
import { BSMessage } from './../../../core/services/BSMessage.service';
import { ActivatedRoute } from "@angular/router";


@Component({
  templateUrl: './suporte-chat.page.html',
  styleUrls: ['./suporte-chat.page.scss'],
})
export class SuporteChatPage implements OnInit {
  idChamado = null
  mensagem = null
  mensagens: any = []
  private sub: any;

  
  constructor(
    private modalCtrl: ModalController,
    private chamadoResource: ChamadoResource,
    private bsMessage: BSMessage,
    private route: ActivatedRoute
  ) {
  }
  
  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.idChamado = +params['idChamado']; // (+) converts string 'id' to a number
   });
    this.chamadoResource.getMensagensChamadoByIdChamado(this.idChamado)
      .subscribe(data => {
        this.mensagens = data
      })
  }

  fecharModal() {
    this.modalCtrl.dismiss();
  }

  enviarMensagem() {
    console.log(this.mensagem)
    /*
    this.chamadoResource.gravarMensagem(this.chamado.id, this.chatMensagem)
      .subscribe(response => {
        this.ngOnInit()
    });
    */
  }

}
