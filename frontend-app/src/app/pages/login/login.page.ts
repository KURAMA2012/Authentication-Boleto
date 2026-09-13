import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NavController, ToastController } from '@ionic/angular';
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
    private toastCtrl: ToastController,
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
          const toast = await this.toastCtrl.create({
            message: 'Login realizado com sucesso!',
            duration: 1200,
            color: 'success',
          });
          toast.present();
          this.navCtrl.navigateRoot('/home');
        } else {
          this.erroApi = resp?.message || 'E-mail ou senha inválidos.';
        }
      },
      error: () => {
        this.carregando = false;
        this.erroApi = 'E-mail ou senha inválidos.';
      },
    });
  }

  irParaCadastro() {
    this.navCtrl.navigateForward('/register');
  }
}
