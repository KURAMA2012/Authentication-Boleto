import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ToastController, LoadingController } from '@ionic/angular';
import { BoletoService } from 'src/app/core/services/BoletoService.services';
import { BSMessage } from 'src/app/core/services/BSMessage.service';

@Component({
  selector: 'app-validar-boleto',
  templateUrl: './boleto-validacao.page.html',
  styleUrls: ['./boleto-validacao.page.scss'],
})
export class BoletoValidacaoPage {
  etapa: 'validar' | 'token' | 'confirmar' = 'validar';
  codigoAutenticacao = '';
  tokenVerificacao = '';

  progresso = 33;
  indiceEtapa = 1;
  descricaoEtapa = 'Validar Boleto';

  constructor(private boletoService: BoletoService, private bsMessage: BSMessage) {}

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
        this.descricaoEtapa = 'Enviar Token';
        break;
      case 'confirmar':
        this.progresso = 100;
        this.indiceEtapa = 3;
        this.descricaoEtapa = 'Confirmar Token';
        break;
    }
  }

  validarBoleto() {
    if (!this.codigoAutenticacao) {
      this.bsMessage.alerta('Informe o código de autenticação!');
      return;
    }

    this.boletoService.validarBoleto(this.codigoAutenticacao).subscribe({
      next: (res) => {
        if (res.success) {
          this.bsMessage.sucesso(res.message || 'Boleto validado com sucesso!');
          this.enviarToken();
        } else {
          this.bsMessage.error(res.message || 'Erro ao validar boleto!');
        }
      },
      error: (err) => {
        const msg = err?.error?.message || 'Erro ao validar boleto!';
        this.bsMessage.error(msg);
      },
    });
  }

  enviarToken() {
    this.boletoService.enviarToken(this.codigoAutenticacao).subscribe({
      next: (res) => {
        this.bsMessage.sucesso(res.message || 'Token enviado com sucesso!');
        this.etapa = 'token';
        this.atualizarProgresso();
      },
      error: (err) => {
        const msg = err?.error?.message || 'Erro ao enviar token!';
        this.bsMessage.error(msg);
      },
    });
  }

  confirmarEtapaToken() {
    this.etapa = 'confirmar';
    this.atualizarProgresso();
  }

  confirmarToken() {
    if (!this.tokenVerificacao) {
      this.bsMessage.alerta('Digite o token de verificação!');
      return;
    }

    this.boletoService.confirmarToken(this.codigoAutenticacao, this.tokenVerificacao).subscribe({
      next: (res) => {
        if (res.success) {
          this.bsMessage.sucesso(res.message || 'Token confirmado com sucesso!');
        } else {
          this.bsMessage.error(res.message || 'Token inválido!');
        }
      },
      error: (err) => {
        const msg = err?.error?.message || 'Erro ao confirmar token!';
        this.bsMessage.error(msg);
      },
    });
  }

  voltarEtapa() {
    this.etapa = 'validar';
    this.atualizarProgresso();
  }
}