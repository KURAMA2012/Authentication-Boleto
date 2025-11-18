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

  toggleSenha() {
    this.mostrarSenha = !this.mostrarSenha;
  }

  async login() {

    if (this.loginForm.invalid) {
      const toast = await this.toastCtrl.create({
        message: 'Preencha os campos corretamente!',
        duration: 2000,
        color: 'warning',
      });
      return toast.present();
    }

    const { email, senha } = this.loginForm.value;

    this.auth.login(email, senha).subscribe(
      async (resp) => {

        if (resp.success) {
          const toast = await this.toastCtrl.create({
            message: 'Login realizado com sucesso!',
            duration: 1500,
            color: 'success',
          });

          toast.present();

          toast.onDidDismiss().then(() => {
            this.navCtrl.navigateRoot('/home');
          });
        }
      },
      async () => {
        const toast = await this.toastCtrl.create({
          message: 'Email ou senha inválidos!',
          duration: 2000,
          color: 'danger',
        });
        toast.present();
      }
    );
  }

  irParaCadastro() {
    this.navCtrl.navigateForward('/register');
  }

  
}
