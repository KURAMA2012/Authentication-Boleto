import { Component } from '@angular/core';
import { EmpresaService } from 'src/app/core/services/EmpresaService.services';
import { BSMessage } from 'src/app/core/services/BSMessage.service';
import { LoadingController } from '@ionic/angular';

@Component({
  selector: 'app-cadastro-empresa',
  templateUrl: './cadastro-empresa.page.html',
  styleUrls: ['./cadastro-empresa.page.scss'],
})
export class CadastroEmpresaPage {
  empresa = {
    nome: '',
    fantasia: '',
    cnpj: '',
    email: '',
    telefone: '',
    cep: '',
    logradouro: '',
    numero: '',
    bairro: '',
    municipio: '',
    uf: ''
  };

  carregandoCnpj = false;
  carregandoCep = false;
  carregandoSalvar = false;

  constructor(
    private empresaService: EmpresaService,
    private bsMessage: BSMessage,
    private loadingCtrl: LoadingController
  ) {}

  async buscarCnpj() {
    if (!this.empresa.cnpj) {
      this.bsMessage.alerta('Informe o CNPJ para buscar.');
      return;
    }

    this.carregandoCnpj = true;
    const loading = await this.loadingCtrl.create({
      message: 'Consultando CNPJ...',
      spinner: 'circles',
    });
    await loading.present();

    this.empresaService.buscarCnpj(this.empresa.cnpj).subscribe({
      next: (res) => {
        loading.dismiss();
        this.carregandoCnpj = false;
        if (res.nome) {
          Object.assign(this.empresa, res);
          this.bsMessage.sucesso('Dados do CNPJ carregados com sucesso!');
        } else {
          this.bsMessage.alerta('CNPJ não encontrado.');
        }
      },
      error: () => {
        loading.dismiss();
        this.carregandoCnpj = false;
        this.bsMessage.error('Erro ao consultar CNPJ.');
      }
    });
  }

  async buscarCep() {
    if (!this.empresa.cep) {
      this.bsMessage.alerta('Informe o CEP para buscar endereço.');
      return;
    }

    this.carregandoCep = true;
    const loading = await this.loadingCtrl.create({
      message: 'Buscando endereço...',
      spinner: 'dots',
    });
    await loading.present();

    this.empresaService.buscarCep(this.empresa.cep).subscribe({
      next: (res) => {
        loading.dismiss();
        this.carregandoCep = false;
        if (res.logradouro) {
          this.empresa.logradouro = res.logradouro;
          this.empresa.bairro = res.bairro;
          this.empresa.municipio = res.localidade;
          this.empresa.uf = res.uf;
          this.bsMessage.sucesso('Endereço carregado com sucesso!');
        } else {
          this.bsMessage.alerta('CEP não encontrado.');
        }
      },
      error: () => {
        loading.dismiss();
        this.carregandoCep = false;
        this.bsMessage.error('Erro ao consultar CEP.');
      }
    });
  }

  async cadastrar() {
    if (!this.empresa.nome || !this.empresa.cnpj) {
      this.bsMessage.alerta('Preencha os campos obrigatórios!');
      return;
    }

    this.carregandoSalvar = true;
    const loading = await this.loadingCtrl.create({
      message: 'Salvando empresa...',
      spinner: 'crescent',
    });
    await loading.present();

    
    this.empresaService.cadastrarEmpresa(this.empresa).subscribe({
      next: (res) => {
        loading.dismiss();
        this.carregandoSalvar = false;
        this.bsMessage.sucesso(res.message || 'Empresa cadastrada com sucesso!');
        this.limparFormulario();
      },
      error: (err) => {
        loading.dismiss();
        this.carregandoSalvar = false;
        const msg = err?.error?.message || 'Erro ao cadastrar empresa.';
        this.bsMessage.error(msg);
      }
    });
  }

  limparFormulario() {
    this.empresa = {
      nome: '',
      fantasia: '',
      cnpj: '',
      email: '',
      telefone: '',
      cep: '',
      logradouro: '',
      numero: '',
      bairro: '',
      municipio: '',
      uf: ''
    };
  }
}
