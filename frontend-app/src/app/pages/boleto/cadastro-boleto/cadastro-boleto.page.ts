import { Component } from '@angular/core';
import { HttpClient, HttpEventType } from '@angular/common/http';
import { ToastController, LoadingController } from '@ionic/angular';
import { BoletoService } from 'src/app/core/services/BoletoService.services';
import { BSMessage } from 'src/app/core/services/BSMessage.service';
import { FilePicker } from '@capawesome/capacitor-file-picker';

@Component({
  selector: 'app-cadastro-boleto',
  templateUrl: './cadastro-boleto.page.html',
  styleUrls: ['./cadastro-boleto.page.scss'],
})
export class CadastroBoletoPage {

  modo: 'manual' | 'csv' = 'manual';

  boleto = {
    codigoBarras: '',
    valor: '',
    vencimento: '',
    cpfCnpjPagador: '',
    nomePagador: '',
    cnpjBeneficiario: ''
  };

  arquivoSelecionado?: File;
  carregando = false;
  mensagem: string | null = null;
  erro: boolean = false;

  resposta: any = null;

  constructor(
    private boletoService: BoletoService,
    private toastCtrl: ToastController,
    private bsMessage:BSMessage
  ) {}

  async gerarBoleto() {
    if (
      !this.boleto.codigoBarras ||
      !this.boleto.valor ||
      !this.boleto.vencimento ||
      !this.boleto.cnpjBeneficiario ||
      !this.boleto.nomePagador
    ) {
      const toast = await this.toastCtrl.create({
        message: 'Preencha todos os campos obrigatórios!',
        duration: 2000,
        color: 'warning'
      });
      toast.present();
      return;
    }
    this.boletoService.criarBoleto(this.boleto).subscribe({
      next: async (res) => {
        if (res.success) {
          this.bsMessage.sucesso(res.message || 'Boleto criado com sucesso!');
          this.limparFormulario();
        }
      },
      error: async (err) => {
        const backendMessage = err?.error?.message || 'Erro ao criar boleto!';
        this.bsMessage.error(backendMessage);
      }
    });
  

  }

  limparFormulario() {
        this.boleto = {
          codigoBarras: '',
          valor: '',
          vencimento: '',
          cpfCnpjPagador: '',
          nomePagador: '',
          cnpjBeneficiario: ''
        };
      }

  // =============== UPLOAD DE CSV ===============
async selecionarCsv() {
  try {
    const result = await FilePicker.pickFiles({
      types: ['text/csv']
    });

    if (result.files.length > 0) {
      const file = result.files[0];

      let blob: Blob;
      if (file.blob) {
        blob = file.blob;
      } else if (file.data) {
        const byteCharacters = atob(file.data);
        const byteNumbers = new Array(byteCharacters.length);
        for (let i = 0; i < byteCharacters.length; i++) {
          byteNumbers[i] = byteCharacters.charCodeAt(i);
        }
        blob = new Blob([new Uint8Array(byteNumbers)], { type: 'text/csv' });
      } else {
        this.bsMessage.error('Erro ao ler o arquivo CSV.');
        return;
      }

      // ✅ converte Blob para File antes de enviar
      const arquivo = new File([blob], file.name || 'arquivo.csv', { type: 'text/csv' });

      this.enviarCsv(arquivo);
    }
  } catch (error) {
    console.error('Erro ao selecionar arquivo:', error);
    this.bsMessage.error('Erro ao selecionar o arquivo CSV.');
  }
}


  enviarCsv(file: File) {
    this.carregando = true;

    this.boletoService.uploadCsv(file).subscribe({
      next: (event) => {
        if (event.type === HttpEventType.Response) {
          this.carregando = false;
          this.bsMessage.sucesso('Arquivo CSV enviado e processado com sucesso!');
        }
      },
      error: (err) => {
        this.carregando = false;
        const backendMessage = err?.error?.message || 'Erro ao enviar CSV!';
        this.bsMessage.error(backendMessage);
      }
    });
  }
}