import { Component, OnInit } from '@angular/core';
import { ChamadoResource } from 'src/app/core/services/app-model.services';
import { BSAuth } from 'src/app/core/services/BSAuth.service';
import { BSMessage } from './../../../core/services/BSMessage.service';

@Component({
  selector: 'app-suporte-contato',
  templateUrl: './suporte-contato.page.html',
  styleUrls: ['./suporte-contato.page.scss'],
})
export class SuporteContatoPage implements OnInit {
  chamado = {assunto:null , mensagem:null}
  
  constructor(private chamadoResource: ChamadoResource,
              private bsMessage:BSMessage, 
              private bsAuth:BSAuth  ) { }

  ngOnInit() {
  }

  enviarChamado() {
    const self = this

    if (self.chamado.assunto == null){
      self.bsMessage.alerta("Assunto deve ser selecionado")
      return
    }
    
    if (self.chamado.mensagem == null){
      self.bsMessage.alerta("Mensagem deve ser informada")
      return
    }

    var chamadoDTO = {
      assunto: self.chamado.assunto,
      login: self.bsAuth.usuario.login,
      mensagem: self.chamado.mensagem
    }
    self.chamadoResource.gravarChamado(chamadoDTO)
      .subscribe(response => {
        self.bsMessage.informa("Chamado registrado com sucesso")
        this.novoChamado()
    });
  }

  novoChamado(){
    this.chamado = {assunto:null , mensagem:null}
  }
}
