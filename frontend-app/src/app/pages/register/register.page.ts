import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NavController, ToastController } from '@ionic/angular';
import { AuthService } from 'src/app/core/services/AuthService';


@Component({
  selector: 'app-register',
  templateUrl: './register.page.html',
  styleUrls: ['./register.page.scss'],
})
export class RegisterPage implements OnInit {

  form!: FormGroup;
  mostrarSenha = false;

  constructor(
    private fb: FormBuilder,
    private usuarioService: AuthService,
    private toastCtrl: ToastController,
    private navCtrl: NavController
  ) {}

  ngOnInit() {
    this.form = this.fb.group({
      nome: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      cpf: ['', Validators.required],
      telefone: ['', Validators.required],
      senha: ['', [Validators.required, Validators.minLength(4)]],
    });
  }

  toggleSenha() {
    this.mostrarSenha = !this.mostrarSenha;
  }

  async registrar() {
    if (this.form.invalid) {
      const toast = await this.toastCtrl.create({
        message: 'Preencha todos os campos corretamente.',
        duration: 1800,
        color: 'danger'
      });
      return toast.present();
    }

    this.usuarioService.cadastrar(this.form.value).subscribe(async () => {
      const toast = await this.toastCtrl.create({
        message: 'Cadastro realizado com sucesso!',
        duration: 1500,
        color: 'success'
      });

      toast.present();
      toast.onDidDismiss().then(() => {
        this.navCtrl.navigateRoot('/login');
      });

    }, async () => {
      const toast = await this.toastCtrl.create({
        message: 'Erro ao cadastrar. Tente novamente.',
        duration: 2000,
        color: 'danger'
      });
      toast.present();
    });
  }

}
