import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpEventType } from '@angular/common/http';
import { ToastController } from '@ionic/angular';
import { BoletoService } from 'src/app/core/services/BoletoService.services';
import { FilePicker } from '@capawesome/capacitor-file-picker';

@Component({
  selector: 'app-cadastro-boleto',
  templateUrl: './cadastro-boleto.page.html',
  styleUrls: ['./cadastro-boleto.page.scss'],
})
export class CadastroBoletoPage {
  modo: 'manual' | 'csv' = 'manual';
  form: FormGroup;

  arquivoNome: string | null = null;
  progressoUpload = 0;
  carregando = false;
  erroPainel: string | null = null;
  codigoAutenticacao: string | null = null;

  constructor(
    private fb: FormBuilder,
    private boletoService: BoletoService,
    private toastCtrl: ToastController
  ) {
    this.form = this.fb.group({
      codigoBarras: ['', [Validators.required, Validators.minLength(8)]],
      valor: ['', [Validators.required]],
      vencimento: ['', [Validators.required]],
      cpfCnpjPagador: [''],
      nomePagador: ['', [Validators.required]],
      cnpjBeneficiario: ['', [Validators.required, Validators.minLength(14)]],
    });
  }

  campoInvalido(nome: string): boolean {
    const c = this.form.get(nome);
    return !!(c && c.invalid && (c.dirty || c.touched));
  }

  private async toast(message: string, color: string = 'success') {
    const t = await this.toastCtrl.create({ message, duration: 1800, color });
    await t.present();
  }

  gerarBoleto() {
    this.erroPainel = null;
    this.form.markAllAsTouched();
    if (this.form.invalid || this.carregando) {
      this.erroPainel = 'Preencha os campos obrigatórios.';
      return;
    }

    this.carregando = true;
    this.boletoService.criarBoleto(this.form.value).subscribe({
      next: async (res) => {
        this.carregando = false;
        if (res?.success) {
          this.codigoAutenticacao =
            res?.data?.codigoAutenticacao ||
            res?.codigoAutenticacao ||
            null;
          await this.toast(res.message || 'Boleto criado com sucesso!');
          this.form.reset();
        } else {
          this.erroPainel = res?.message || 'Erro ao criar boleto.';
        }
      },
      error: (err) => {
        this.carregando = false;
        this.erroPainel = err?.error?.message || 'Erro ao criar boleto.';
      },
    });
  }

  async copiarCodigo() {
    if (!this.codigoAutenticacao) {
      return;
    }
    try {
      await navigator.clipboard.writeText(this.codigoAutenticacao);
      await this.toast('Código copiado!');
    } catch {
      await this.toast('Não foi possível copiar.', 'warning');
    }
  }

  gerarOutro() {
    this.codigoAutenticacao = null;
    this.erroPainel = null;
  }

  async selecionarCsv() {
    this.erroPainel = null;
    this.arquivoNome = null;
    this.progressoUpload = 0;

    try {
      const result = await FilePicker.pickFiles({ types: ['text/csv'] });
      if (!result.files.length) {
        return;
      }

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
        this.erroPainel = 'Erro ao ler o arquivo CSV.';
        return;
      }

      const nome = file.name || 'arquivo.csv';
      this.arquivoNome = nome;
      const arquivo = new File([blob], nome, { type: 'text/csv' });
      this.enviarCsv(arquivo);
    } catch (error) {
      console.error(error);
      this.erroPainel = 'Erro ao selecionar o arquivo CSV.';
    }
  }

  enviarCsv(file: File) {
    this.carregando = true;
    this.progressoUpload = 0;

    this.boletoService.uploadCsv(file).subscribe({
      next: (event) => {
        if (event.type === HttpEventType.UploadProgress && event.total) {
          this.progressoUpload = Math.round((100 * event.loaded) / event.total);
        }
        if (event.type === HttpEventType.Response) {
          this.carregando = false;
          this.progressoUpload = 100;
          this.toast('CSV enviado. Acompanhe o processamento em Logs.');
        }
      },
      error: (err) => {
        this.carregando = false;
        this.progressoUpload = 0;
        this.erroPainel = err?.error?.message || 'Erro ao enviar CSV.';
      },
    });
  }
}
