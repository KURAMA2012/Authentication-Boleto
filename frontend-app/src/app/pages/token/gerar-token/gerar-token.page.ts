import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ToastController } from '@ionic/angular';

@Component({
  selector: 'app-gerar-token',
  templateUrl: './gerar-token.page.html',
  styleUrls: ['./gerar-token.page.scss'],
})
export class GerarTokenPage {

  codigoAutenticacao: string = '';
  token: string = '';

  constructor(private http: HttpClient, private toastCtrl: ToastController) {}

  async gerarToken() {
    if (!this.codigoAutenticacao) {
      const toast = await this.toastCtrl.create({
        message: 'Informe o código de autenticação!',
        duration: 2000,
        color: 'warning'
      });
      toast.present();
      return;
    }

    this.http.post<any>('http://localhost:8080/boletos/enviar-token', { codigoAutenticacao: this.codigoAutenticacao })
      .subscribe({
        next: (res) => {
          this.token = res.data || 'Token gerado com sucesso!';
        },
        error: async (err) => {
          const toast = await this.toastCtrl.create({
            message: 'Erro ao gerar token!',
            duration: 2000,
            color: 'danger'
          });
          toast.present();
        }
      });
  }
}
