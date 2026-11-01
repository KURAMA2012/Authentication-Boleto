import { Component, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { somenteDigitos } from 'src/app/shared/documento.util';
import { FeedbackService } from 'src/app/shared/feedback.service';
import { ValidacaoPublicaResultado, ValidacaoPublicaService } from 'src/app/core/services/ValidacaoPublicaService';

/** Segundos de espera entre envios do codigo por e-mail. */
const ESPERA_REENVIO_S = 30;

const MSG_TOKEN_INVALIDO = 'C\u00f3digo inv\u00e1lido ou expirado.';
const MSG_LIMITE = 'Muitas tentativas, aguarde alguns minutos.';

/** Validacao publica do boleto pelo pagador (sem login), com confirmacao por codigo no e-mail. */
@Component({
  selector: 'app-validar',
  templateUrl: './validar.page.html',
  styleUrls: ['./validar.page.scss'],
})
export class ValidarPage implements OnDestroy {
  form: FormGroup;
  tokenForm: FormGroup;
  carregando = false;
  confirmando = false;
  reenviando = false;
  resultado: ValidacaoPublicaResultado | null = null;
  confirmado = false;
  erroPainel: string | null = null;
  erroToken: string | null = null;
  esperaReenvio = 0;

  private codigoConsultado = '';
  private emailConsultado = '';
  private timer: any = null;

  constructor(
    private fb: FormBuilder,
    private validacao: ValidacaoPublicaService,
    private feedback: FeedbackService
  ) {
    this.form = this.fb.group({
      codigoBoleto: ['', [Validators.required]],
      emailPagador: ['', [Validators.required, Validators.email]],
      cnpjBeneficiario: [''],
    });
    this.tokenForm = this.fb.group({
      token: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
    });
  }

  get etapaToken(): boolean {
    return !!this.resultado && this.resultado.veredito === 'AUTENTICO' && this.resultado.tokenEnviado && !this.confirmado;
  }

  campoInvalido(nome: string, grupo: FormGroup = this.form): boolean {
    const c = grupo.get(nome);
    return !!(c && c.invalid && (c.dirty || c.touched));
  }

  /** Mantem so digitos no campo do codigo (max 6). */
  limparToken() {
    const c = this.tokenForm.get('token');
    const v = somenteDigitos(c?.value || '').slice(0, 6);
    if (c && c.value !== v) c.setValue(v);
  }

  validar() {
    this.erroPainel = null;
    this.resultado = null;
    this.form.markAllAsTouched();
    if (this.form.invalid || this.carregando) return;

    const v = this.form.value;
    const cnpj = somenteDigitos(v.cnpjBeneficiario || '');
    this.codigoConsultado = String(v.codigoBoleto).trim();
    this.emailConsultado = String(v.emailPagador).trim();
    this.carregando = true;
    this.validacao
      .validar({
        codigoBoleto: this.codigoConsultado,
        emailPagador: this.emailConsultado,
        ...(cnpj ? { cnpjBeneficiario: cnpj } : {}),
      })
      .subscribe({
        next: (res) => {
          this.carregando = false;
          this.resultado = res.data;
          if (this.etapaToken) this.iniciarEspera();
        },
        error: (err) => {
          this.carregando = false;
          this.erroPainel = this.mensagem(err, 'N\u00e3o foi poss\u00edvel validar o boleto agora.');
          this.feedback.erro(err, this.erroPainel);
        },
      });
  }

  confirmar() {
    this.erroToken = null;
    this.tokenForm.markAllAsTouched();
    if (this.tokenForm.invalid || this.confirmando) return;
    this.confirmando = true;
    this.validacao.confirmar(this.codigoConsultado, this.tokenForm.value.token).subscribe({
      next: () => {
        this.confirmando = false;
        this.confirmado = true;
        this.pararEspera();
        this.feedback.sucesso('Boleto confirmado.');
      },
      error: (err) => {
        this.confirmando = false;
        this.erroToken = this.mensagem(err, 'N\u00e3o foi poss\u00edvel confirmar o c\u00f3digo agora.');
      },
    });
  }

  reenviar() {
    if (this.esperaReenvio > 0 || this.reenviando) return;
    this.erroToken = null;
    this.reenviando = true;
    this.validacao.enviarToken(this.codigoConsultado, this.emailConsultado).subscribe({
      next: () => {
        this.reenviando = false;
        this.iniciarEspera();
        this.feedback.sucesso('Enviamos um novo c\u00f3digo para o seu e-mail.');
      },
      error: (err) => {
        this.reenviando = false;
        this.erroToken = this.mensagem(err, 'N\u00e3o foi poss\u00edvel reenviar o c\u00f3digo agora.');
      },
    });
  }

  novaConsulta() {
    this.pararEspera();
    this.resultado = null;
    this.confirmado = false;
    this.erroPainel = null;
    this.erroToken = null;
    this.form.reset();
    this.tokenForm.reset();
  }

  ngOnDestroy() {
    this.pararEspera();
  }

  /** Decide pela propriedade `code` do envelope (status HTTP como reserva), nunca pelo texto. */
  private mensagem(err: unknown, fallback: string): string {
    if (err instanceof HttpErrorResponse) {
      const code = err.error && typeof err.error === 'object' ? err.error.code : undefined;
      if (code === 'LIMITE_TENTATIVAS' || (!code && err.status === 429)) return MSG_LIMITE;
      // 404: o back atual responde assim para codigo de boleto sem token pendente.
      if (code === 'TOKEN_INVALIDO' || (!code && (err.status === 401 || err.status === 404))) return MSG_TOKEN_INVALIDO;
      if (code === 'VALIDACAO_CAMPO' || err.status === 400) {
        return (err.error && err.error.message) || fallback;
      }
    }
    return this.feedback.mensagemDeErro(err, fallback);
  }

  private iniciarEspera() {
    this.pararEspera();
    this.esperaReenvio = ESPERA_REENVIO_S;
    this.timer = setInterval(() => {
      this.esperaReenvio--;
      if (this.esperaReenvio <= 0) this.pararEspera();
    }, 1000);
  }

  private pararEspera() {
    if (this.timer) clearInterval(this.timer);
    this.timer = null;
    this.esperaReenvio = 0;
  }
}