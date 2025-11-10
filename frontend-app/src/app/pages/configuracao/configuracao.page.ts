import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AlertController, LoadingController, MenuController, NavController, ToastController } from '@ionic/angular';
import { BSAppService } from 'src/app/core/services/BSApp.services';
import { BSLoading } from 'src/app/core/services/BSLoading.service';
import { RAJResource } from 'src/app/core/services/raj-model.services';
import { BSMessage } from '../../core/services/BSMessage.service';




@Component({
  selector: 'app-configuracao',
  templateUrl: './configuracao.page.html',
  styleUrls: ['./configuracao.page.scss'],
})
export class ConfiguracaoPage implements OnInit {
  public onLoginForm: FormGroup;
  public onFormServer2: FormGroup;

  input_url1: any = ''
  input_porta1: any = null
  input_url2: any = ''
  input_porta2: any = null
  tokenDevice = null

  isAppConnected: boolean = false
  version
  mostrarCamposServer1:boolean = true
  mostrarCamposServer2:boolean = false
  constructor(
    public navCtrl: NavController,
    public menuCtrl: MenuController,
    public toastCtrl: ToastController,
    public alertCtrl: AlertController,
    public loadingCtrl: LoadingController,
    private formBuilder: FormBuilder,
    private bsMessage: BSMessage,
    private bsApp: BSAppService,
    private rajResource: RAJResource,
    private bsLoading: BSLoading,
    private route: ActivatedRoute
  ) { }

  ionViewWillEnter() {
    this.menuCtrl.enable(false);
  }

  ngOnInit() {
    
    this.tokenDevice = this.bsApp.getTokenDevice()
    console.log('congfirurandao')
    this.onLoginForm = this.formBuilder.group({
      'input_url1': [null, Validators.compose([
        Validators.required
      ])],
      'input_porta1': [null, Validators.compose([
        Validators.required
      ])]
    });

    this.onFormServer2 = this.formBuilder.group({
      'input_url2': [null, Validators.compose([
        Validators.required
      ])],
      'input_porta2': [null, Validators.compose([
        Validators.required
      ])]
    });

    this.input_url1 = this.bsApp.getConfigServer1Url()
    this.input_porta1 = this.bsApp.getConfigServer1Porta()

    this.input_url2 = this.bsApp.getConfigServer2Url()
    this.input_porta2 = this.bsApp.getConfigServer2Porta()

    if (this.input_url1 == null || this.input_url2 == 'null'){
      this.input_url1 = '' 
      this.input_url2 = '' 
     
    }

  }
  goToRegister() {
    this.navCtrl.navigateRoot('/register');
  }

  retornarParaLogin(){
    this.navCtrl.navigateBack('/login')
  }

  registrarConfiguracoes_e_retornarParaLogin(){

    // if (this.input_url1 == null || this.input_url2 == 'null'){
    //   this.input_url1 = '' 
    //   this.input_url2 = '' 
    // }

    if (this.input_url1 == null || this.input_url1 == 'null'){
      this.input_url1 = ''      
    }
    if (this.input_url2 == null || this.input_url2 == 'null'){
      this.input_url2 = ''      
    }
    
    if (this.input_porta1 == null || this.input_porta1 == 'null'){
      this.input_porta1 = ''      
    }
    
    if (this.input_porta2 == null || this.input_porta2 == 'null'){
      this.input_porta2 = ''      
    }
    
    this.bsApp.SalvarConfiguracao(this.input_url1.trim(), this.input_porta1, this.input_url2.trim(), this.input_porta2)
    this.retornarParaLogin()
  }
 
}
