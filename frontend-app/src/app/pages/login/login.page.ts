import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NavController } from '@ionic/angular';
import { FeedbackService } from 'src/app/shared/feedback.service';
import { AuthService } from 'src/app/core/services/AuthService';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage {
  loginForm: FormGroup;
  mostrarSenha = false;
  carregando = false;
  erroApi: string | null = null;

  constructor(
    private fb: FormBuilder,
    private feedback: FeedbackService,
    private navCtrl: NavController,
    private auth: AuthService
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(4)]],
    });
  }

  get email() {
    return this.loginForm.get('email');
  }

  get senha() {
    return this.loginForm.get('senha');
  }

  toggleSenha() {
    this.mostrarSenha = !this.mostrarSenha;
  }

  campoInvalido(nome: 'email' | 'senha'): boolean {
    const c = this.loginForm.get(nome);
    return !!(c && c.invalid && (c.dirty || c.touched));
  }

  async login() {
    this.erroApi = null;
    this.loginForm.markAllAsTouched();

    if (this.loginForm.invalid || this.carregando) {
      return;
    }

    const { email, senha } = this.loginForm.value;
    this.carregando = true;

    this.auth.login(email, senha).subscribe({
      next: async (resp) => {
        this.carregando = false;
        if (resp?.success) {
          this.feedback.sucesso('Login realizado com sucesso!');
          this.navCtrl.navigateRoot('/home');
        } else {
          this.erroApi = resp?.message || 'E-mail ou senha inválidos.';
        }
      },
      error: (err) => {
        this.carregando = false;
        this.erroApi = this.feedback.mensagemDeErro(err, 'E-mail ou senha inválidos.');
      },
    });
  }

  irParaCadastro() {
    this.navCtrl.navigateForward('/register');
  }
}
