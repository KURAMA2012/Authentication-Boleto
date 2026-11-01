import { somenteDigitos } from 'src/app/shared/documento.util';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpEventType } from '@angular/common/http';
import { FeedbackService } from 'src/app/shared/feedback.service';
import { BoletoService } from 'src/app/core/services/BoletoService.services';
import { FilePicker } from '@capawesome/capacitor-file-picker';

@Component({
  selector: 'app-cadastro-boleto',
  templateUrl: './cadastro-boleto.page.html',
  styleUrls: ['./cadastro-boleto.page.scss'],
})
export class CadastroBoletoPage {
  modo: 'manual' | 'csv' = 'manual';
  /** Hoje em yyyy-MM-dd pela data LOCAL (toISOString() usaria UTC e erraria a noite). */
  get hoje(): string {
    const d = new Date();
    const p = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}`;
  }
  form: FormGroup;

  arquivoNome: string | null = null;
  progressoUpload = 0;
  carregando = false;
  erroPainel: string | null = null;
  codigoAutenticacao: string | null = null;

  constructor(
    private fb: FormBuilder,
    private boletoService: BoletoService,
    private feedback: FeedbackService
  ) {
    this.form = this.fb.group({
      codigoBarras: ['', [Validators.required, Validators.minLength(8)]],
      valor: ['', [Validators.required]],
      vencimento: ['', [Validators.required, (c: any) => (c.value && this.paraDataApi(c.value) < this.hoje ? { passado: true } : null)]],
      cpfCnpjPagador: [''],
      nomePagador: ['', [Validators.required]],
      cnpjBeneficiario: ['', [Validators.required, (c: any) => somenteDigitos(c.value).length === 14 ? null : { cnpj: true }]],
    });
  }

  campoInvalido(nome: string): boolean {
    const c = this.form.get(nome);
    return !!(c && c.invalid && (c.dirty || c.touched));
  }

  /** ion-datetime devolve ISO (yyyy-MM-ddTHH:mm...); a API recebe yyyy-MM-dd (igual ao antigo input type=date). */
  /** Texto exibido no campo de vencimento: dd/MM/yyyy ou 'Selecionar data' quando vazio (inclusive apos form.reset()). */
  get vencimentoExibicao(): string {
    const v = this.paraDataApi(this.form.get('vencimento')?.value);
    if (!v) { return 'Selecionar data'; }
    const [a, m, d] = v.split('-');
    return `${d}/${m}/${a}`;
  }

  private paraDataApi(valor: string | null | undefined): string {
    return valor ? String(valor).substring(0, 10) : '';
  }

  private async toast(message: string, color: string = 'success') {
    if (color === 'success') { await this.feedback.sucesso(message); } else { await this.feedback.erro(null, message); }
  }

  gerarBoleto() {
    this.erroPainel = null;
    this.form.markAllAsTouched();
    if (this.form.invalid || this.carregando) {
      this.erroPainel = 'Preencha os campos obrigatórios.';
      return;
    }

    this.carregando = true;
    this.boletoService.criarBoleto({
      ...this.form.value,
      vencimento: this.paraDataApi(this.form.value.vencimento),
      cpfCnpjPagador: somenteDigitos(this.form.value.cpfCnpjPagador),
      cnpjBeneficiario: somenteDigitos(this.form.value.cnpjBeneficiario),
    }).subscribe({
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
        this.erroPainel = this.feedback.mensagemDeErro(err, 'Erro ao criar boleto.');
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
        this.erroPainel = this.feedback.mensagemDeErro(err, 'Erro ao enviar CSV.');
      },
    });
  }
}
