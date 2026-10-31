import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastController } from '@ionic/angular';

/**
 * Toasts padronizados do app.
 * Le `message` (contrato atual) e `code` (contrato de erro novo, quando existir).
 */
@Injectable({ providedIn: 'root' })
export class FeedbackService {
  constructor(private toastCtrl: ToastController) {}

  async sucesso(message: string) {
    await this.mostrar(message, 'success', 'checkmark-circle-outline');
  }

  async erro(err: unknown, fallback = 'Algo deu errado. Tente novamente.') {
    await this.mostrar(this.mensagemDeErro(err, fallback), 'danger', 'alert-circle-outline');
  }

  mensagemDeErro(err: unknown, fallback: string): string {
    if (err instanceof HttpErrorResponse) {
      if (err.status === 0) return 'Sem conexao com a API. Verifique se o backend esta rodando.';
      const body: any = err.error;
      if (body && typeof body === 'object' && body.message) {
        return body.code ? `${body.message} (${body.code})` : body.message;
      }
      if (typeof body === 'string' && body.trim()) return body;
    }
    return fallback;
  }

  private async mostrar(message: string, color: string, icon: string) {
    const t = await this.toastCtrl.create({
      message, color, icon, duration: 2600, position: 'top', cssClass: 'ab-toast',
      buttons: [{ icon: 'close-outline', role: 'cancel' }],
    });
    await t.present();
  }
}