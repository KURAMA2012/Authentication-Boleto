import { Component, Input, OnInit, ViewChild, ElementRef } from "@angular/core";
import { ModalController } from "@ionic/angular";
import { ChamadoResource } from './../../../core/services/app-model.services';
import { BSMessage } from './../../../core/services/BSMessage.service';


@Component({
  templateUrl: './suporte-chat.html',
  styleUrls: ['./suporte-chat.scss'],
})
export class SuporteChatComponent implements OnInit {
  @Input() titulo: string
  @Input() chamado: any

  mensagem = null
  mensagens: any = []

  @ViewChild('txtMensagem') txtMensagem

  
  constructor(
    private modalCtrl: ModalController,
    private chamadoResource: ChamadoResource,
    private bsMessage: BSMessage
  ) {
  }
  
  ngOnInit() {
    this.chamadoResource.getMensagensChamadoByIdChamado(this.chamado.id)
      .subscribe(data => {
        this.mensagens = data
      })
  }

  fecharModal() {
    this.modalCtrl.dismiss();
  }

  enviarMensagem() {

    if (this.txtMensagem.value.length == 0 ){
      this.bsMessage.informa("Mensagem deve ser informada...")
      return
    }
    this.chamadoResource.gravarMensagem(this.chamado.id, this.txtMensagem.value)
      .subscribe(response => {
        this.ngOnInit()
        this.txtMensagem.value = null
    });
  }
}
