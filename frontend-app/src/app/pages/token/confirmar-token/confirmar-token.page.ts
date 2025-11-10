import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ToastController, LoadingController } from '@ionic/angular';

@Component({
  selector: 'app-confirmar-token',
  templateUrl: './confirmar-token.page.html',
  styleUrls: ['./confirmar-token.page.scss'],
})
export class ConfirmarTokenPage {
  codigoAutenticacao: string = '';
  tokenVerificacao: string = '';
  resultado: string | null = null;
  private apiUrl = 'http://localhost:8080/api/boleto/confirmar';

  constructor(
    private http: HttpClient,
    private toastCtrl: ToastController,
    private loadingCtrl: LoadingController
  ) {}

  async confirmarToken() {
    if (!this.codigoAutenticacao || !this.tokenVerificacao) {
      this.showToast('Preencha todos os campos.');
      return;
    }

    const loading = await this.loadingCtrl.create({ message: 'Confirmando token...' });
    await loading.present();

    const payload = {
      codigoAutenticacao: this.codigoAutenticacao,
      tokenVerificacao: this.tokenVerificacao
    };

    this.http.post(this.apiUrl, payload).subscribe({
      next: async (res: any) => {
        await loading.dismiss();
        this.resultado = res.message;
        this.showToast(res.message || 'Confirmação concluída!');
      },
      error: async (err) => {
        await loading.dismiss();
        this.showToast('Token inválido ou boleto não encontrado.');
        console.error(err);
      }
    });
  }

  async showToast(message: string) {
    const toast = await this.toastCtrl.create({
      message,
      duration: 2500,
      color: 'dark',
      position: 'bottom'
    });
    await toast.present();
  }
}
