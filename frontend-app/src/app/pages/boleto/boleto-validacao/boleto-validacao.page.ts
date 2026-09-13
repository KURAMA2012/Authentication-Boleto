import { Component } from '@angular/core';
import { ToastController } from '@ionic/angular';
import { BoletoService } from 'src/app/core/services/BoletoService.services';

type Etapa = 'validar' | 'token' | 'confirmar' | 'sucesso';

@Component({
  selector: 'app-validar-boleto',
  templateUrl: './boleto-validacao.page.html',
  styleUrls: ['./boleto-validacao.page.scss'],
})
export class BoletoValidacaoPage {
  etapa: Etapa = 'validar';
  codigoAutenticacao = '';
  tokenVerificacao = '';

  progresso = 33;
  indiceEtapa = 1;
  descricaoEtapa = 'Validar Boleto';

  carregando = false;
  erroPainel: string | null = null;

  constructor(
    private boletoService: BoletoService,
    private toastCtrl: ToastController
  ) {}

  private atualizarProgresso() {
    switch (this.etapa) {
      case 'validar':
        this.progresso = 33;
        this.indiceEtapa = 1;
        this.descricaoEtapa = 'Validar Boleto';
        break;
      case 'token':
        this.progresso = 66;
        this.indiceEtapa = 2;
        this.descricaoEtapa = 'Token enviado';
        break;
      case 'confirmar':
        this.progresso = 100;
        this.indiceEtapa = 3;
        this.descricaoEtapa = 'Confirmar Token';
        break;
      case 'sucesso':
        this.progresso = 100;
        this.indiceEtapa = 3;
        this.descricaoEtapa = 'Concluído';
        break;
    }
  }

  private async toast(message: string, color: string = 'success') {
    const t = await this.toastCtrl.create({ message, duration: 1600, color });
    await t.present();
  }

  validarBoleto() {
    this.erroPainel = null;

    if (!this.codigoAutenticacao?.trim()) {
      this.erroPainel = 'Informe o código de autenticação.';
      return;
    }
    if (this.carregando) {
      return;
    }

    this.carregando = true;
    this.boletoService.validarBoleto(this.codigoAutenticacao.trim()).subscribe({
      next: (res) => {
        this.carregando = false;
        if (res?.success) {
          this.toast(res.message || 'Boleto validado.');
          this.enviarToken();
        } else {
          this.erroPainel = res?.message || 'Não foi possível validar o boleto.';
        }
      },
      error: (err) => {
        this.carregando = false;
        this.erroPainel =
          err?.error?.message ||
          'Boleto rejeitado ou código inválido. Verifique e tente de novo.';
      },
    });
  }

  enviarToken() {
    this.carregando = true;
    this.boletoService.enviarToken(this.codigoAutenticacao.trim()).subscribe({
      next: (res) => {
        this.carregando = false;
        this.toast(res?.message || 'Token enviado (simulado).');
        this.etapa = 'token';
        this.atualizarProgresso();
      },
      error: (err) => {
        this.carregando = false;
        this.erroPainel = err?.error?.message || 'Erro ao enviar o token.';
      },
    });
  }

  reenviarToken() {
    this.erroPainel = null;
    this.enviarToken();
  }

  confirmarEtapaToken() {
    this.erroPainel = null;
    this.etapa = 'confirmar';
    this.atualizarProgresso();
  }

  confirmarToken() {
    this.erroPainel = null;

    if (!this.tokenVerificacao?.trim()) {
      this.erroPainel = 'Digite o token de verificação.';
      return;
    }
    if (this.carregando) {
      return;
    }

    this.carregando = true;
    this.boletoService
      .confirmarToken(this.codigoAutenticacao.trim(), this.tokenVerificacao.trim())
      .subscribe({
        next: (res) => {
          this.carregando = false;
          if (res?.success) {
            this.etapa = 'sucesso';
            this.atualizarProgresso();
            this.toast(res.message || 'Boleto autenticado!');
          } else {
            this.erroPainel = res?.message || 'Token inválido ou expirado.';
          }
        },
        error: (err) => {
          this.carregando = false;
          this.erroPainel =
            err?.error?.message || 'Token inválido ou expirado. Tente novamente.';
        },
      });
  }

  voltarEtapa() {
    this.erroPainel = null;
    if (this.etapa === 'confirmar') {
      this.etapa = 'token';
    } else {
      this.etapa = 'validar';
    }
    this.atualizarProgresso();
  }

  reiniciar() {
    this.codigoAutenticacao = '';
    this.tokenVerificacao = '';
    this.erroPainel = null;
    this.etapa = 'validar';
    this.atualizarProgresso();
  }
}
