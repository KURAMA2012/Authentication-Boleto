import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ToastController } from '@ionic/angular';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage {
  loginForm: FormGroup;
  mostrarSenha = false;

  constructor(private fb: FormBuilder, private toastCtrl: ToastController) {
    this.loginForm = this.fb.group({
      nome: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(4)]],
    });
  }

  toggleSenha() {
    this.mostrarSenha = !this.mostrarSenha;
  }

  async login() {
    if (this.loginForm.invalid) {
      const toast = await this.toastCtrl.create({
        message: 'Preencha todos os campos!',
        duration: 2000,
        color: 'warning',
      });
      toast.present();
      return;
    }

    const toast = await this.toastCtrl.create({
      message: 'Cadastro realizado com sucesso!',
      duration: 2000,
      color: 'success',
    });
    toast.present();
  }

  async loginRapido() {
    const toast = await this.toastCtrl.create({
      message: 'Login realizado com sucesso!',
      duration: 2000,
      color: 'success',
    });
    toast.present();
  }
}
