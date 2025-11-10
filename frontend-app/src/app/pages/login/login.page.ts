import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AlertController, LoadingController, MenuController, NavController, Platform, ToastController } from '@ionic/angular';
import { BSAuth } from './../../core/services/BSAuth.service';
import { BSMessage } from './../../core/services/BSMessage.service';
import { BSAppService } from 'src/app/core/services/BSApp.services';
import { BSLoading } from 'src/app/core/services/BSLoading.service';
import { NativeBiometric, BiometryType } from '@capgo/capacitor-native-biometric';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage implements OnInit {

  version: string | null = null;
  onLoginForm: FormGroup;
  codigoOperador: string | null = null;
  senha: string | null = null;
  lembrarUsuario: boolean | null = null;

  inp_statServer: any = null;
  statusDoServidor: number = 0;
  bloquearLogin = false;

  cSucessos = 0;
  cContadorDeErros = 0;
  pararTimeOut = false;

  @ViewChild("viewLogin") viewLogin: ElementRef;

  constructor(
    private navCtrl: NavController,
    private menuCtrl: MenuController,
    private toastCtrl: ToastController,
    private alertCtrl: AlertController,
    private loadingCtrl: LoadingController,
    private formBuilder: FormBuilder,
    private bsAuth: BSAuth,
    private bsMessage: BSMessage,
    private bsAppRaj: BSAppService,
    private platform: Platform,
    private bsApp: BSAppService,
    private bsLoading: BSLoading
  ) {}

  ngOnInit() {
    this.initForm();
    this.loadSavedCredentials();
    this.version = this.bsAppRaj.version;
    this.pararTimeOut = false;
    this.fluxoTesteDaConexao(3000);
  }

  ionViewWillEnter() {
    this.menuCtrl.enable(false);
    this.atualizaFooterStatusServico();
  }

  private initForm() {
    this.onLoginForm = this.formBuilder.group({
      codigoOperador: [null, Validators.required],
      senha: [null, Validators.required],
      lembrarUsuario: [null, Validators.required]
    });
  }

  private loadSavedCredentials() {
    if (this.bsAppRaj.isLembrarUsuario()) {
      this.codigoOperador = this.bsAppRaj.getUsuarioLembrar();
      this.senha = this.bsAppRaj.getSenhaLembrar();
      this.lembrarUsuario = true;
    } else {
      this.codigoOperador = null;
      this.senha = null;
      this.lembrarUsuario = false;
    }
  }

  scrollIntoView() {
    // this.viewLogin.nativeElement.scrollIntoView({ behavior: "smooth", block: "start" });
  }

  private fluxoTesteDaConexao(nTimeOut: number) {
    let cTimeOut = 800;
    setTimeout(() => {
      this.bsAppRaj.TestarConexao();
      this.atualizaFooterStatusServico();
      this.bsAppRaj.LerConfiguracao();
      this.atualizaFooterStatusServico();

      if (this.bsAppRaj.EstaConectado()) {
        cTimeOut = 5000;
        this.cSucessos++;
        if (this.cSucessos > 5) {
          this.cSucessos = 0;
          cTimeOut = 1500;
          this.bsAppRaj.ForcarNovoTesteDeConexao();
        }
      } else if (this.bsAppRaj.EstaComFalhaDeConexaoEmAmbosOsServers()) {
        cTimeOut = 1500;
        this.cContadorDeErros++;
        if (this.cContadorDeErros > 5) {
          this.cContadorDeErros = 0;
          this.bsAppRaj.ForcarNovoTesteDeConexao();
        }
      } else {
        cTimeOut = 800;
      }

      console.log(`${this.cSucessos} - ${this.pararTimeOut} ms ${cTimeOut} - Proximo teste: ${this.bsAppRaj.StatusServerNome()}`);

      if (!this.pararTimeOut) this.fluxoTesteDaConexao(cTimeOut);
    }, nTimeOut);
  }

  goToRegister() {
    this.navCtrl.navigateRoot('/register');
  }

  async loginTeste() {
    this.navCtrl.navigateRoot('home-results');
  }

  async login() {
    this.pararTimeOut = true;
    this.bsLoading.loading();

    try {
      if (this.bsAppRaj.isHabilitarAcessoDigitalConfigurado() && this.bsAppRaj.isHabilitarAcessoDigital()) {
        await this.logarComDigital();
      } else {
        await this.logarRaj();
      }
    } finally {
      this.bsLoading.unloading();
    }
  }

  private async logarRaj() {
    if (!this.codigoOperador || !this.senha) {
      this.bsMessage.informa("Usuário e Senha devem ser informados");
      return;
    }
    try {
      await this.bsAuth.autentidarRAJ(this.codigoOperador, this.senha, this.lembrarUsuario);
    } catch (e) {
      this.bsMessage.error("Acesso não autorizado");
    }
  }

 private async logarComDigital() {
  try {
    await this.platform.ready();

    const available = await NativeBiometric.isAvailable();

    // Ajuste para tipos válidos na versão atual
    if (
      available.biometryType === BiometryType.FINGERPRINT ||
      available.biometryType === BiometryType.FACE_AUTHENTICATION
    ) {
      await NativeBiometric.verifyIdentity({ reason: 'Autenticação biométrica' });

      await this.bsAuth.autentidarRAJ(
        this.bsAppRaj.getUsuarioAcessoDigital(),
        this.bsAppRaj.getSenhaAcessoDigital(),
        false
      );
    } else {
      await this.logarRaj();
    }

  } catch (err) {
    console.error('Erro na autenticação biométrica:', err);
    await this.logarRaj();
  }
}

  configurar() {
    this.pararTimeOut = true;
    this.navCtrl.navigateForward('/configuracao/alterar');
  }

  atualizaFooterStatusServico() {
    const stSrv = this.bsAppRaj.getStatusServerInteger();
    this.inp_statServer = this.bsAppRaj.StatusServerCor();
    this.statusDoServidor = stSrv;
    this.bloquearLogin = (stSrv !== 1);
    console.log(`stSrv ${stSrv} -- ${this.bsAppRaj.StatusServerCor()} -- ${this.bsAppRaj.StatusServerNome()}`);
  }

}
