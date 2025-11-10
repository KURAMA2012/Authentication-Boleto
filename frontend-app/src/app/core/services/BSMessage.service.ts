import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AlertController } from '@ionic/angular';
import { BSLoading } from './BSLoading.service';
import { RAJResource } from 'src/app/core/services/raj-model.services';

@Injectable()
export class BSMessage {

  constructor(

    public alertController: AlertController,
    private bsLoagind:BSLoading,
    
    ){ }

    sucesso(mensagem: String) {
        this.mensagem('Aviso',mensagem )
    }

    informa(mensagem: String) {
      this.mensagem('Aviso',mensagem )
    }

    alerta(mensagem: String) {
      this.mensagem('Alerta',mensagem )
    }

    error(mensagem: String) {
      this.mensagem('Error',mensagem )
    }

    async mensagem(titulo, mensagem) {
      const alert = await this.alertController.create({
        header: titulo,
        message: mensagem,
        buttons: ['OK']
      });
  
      await alert.present();
    }

    async confirma(mensagem, handlerOk) {
      const alert = await this.alertController.create({
        header: 'Confirma!',
        message: mensagem,
        buttons: [
          {
            text: 'Não',
            role: 'cancel',
            cssClass: 'secondary'
          }, {
            text: 'Sim',
            handler: () => {
              handlerOk()
            }
          }
        ]
      });
  
      await alert.present();
    }

async errorOnRequest(error: HttpErrorResponse) {
  const backendMessage = error?.error?.message;
  if (backendMessage) {
    await this.error(backendMessage);
    return;
  }
  
  const statusCode = error.status;
  if (statusCode === 409) {
    await this.error('Conflito detectado. Verifique o código de barras.');
  } else if (statusCode === 500) {
    await this.error('Erro interno no servidor.');
  } else if (statusCode === 404) {
    await this.error('Recurso não encontrado.');
  } else {
    await this.error('Erro inesperado. Tente novamente.');
  }
}

}
