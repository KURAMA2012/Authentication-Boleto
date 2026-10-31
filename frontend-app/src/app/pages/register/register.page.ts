import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NavController } from '@ionic/angular';
import { FeedbackService } from 'src/app/shared/feedback.service';
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
    private feedback: FeedbackService,
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
      return this.feedback.erro(null, 'Preencha todos os campos corretamente.');
    }

    this.usuarioService.cadastrar(this.form.value).subscribe(async () => {
      await this.feedback.sucesso('Cadastro realizado com sucesso!');
      this.navCtrl.navigateRoot('/login');

    }, async (err) => {
      await this.feedback.erro(err, 'Erro ao cadastrar. Tente novamente.');
    });
  }

}
