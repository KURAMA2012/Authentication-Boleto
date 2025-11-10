import { Component, AfterViewInit, OnInit } from '@angular/core';
import { AlertController, MenuController, ModalController, NavController, PopoverController, ToastController } from '@ionic/angular';
import { NotificationsComponent } from './../../components/notifications/notifications.component';
import { TituloResource } from './../../core/services/app-model.services';
import { BSAuth } from './../../core/services/BSAuth.service';
import { BSMessage } from './../../core/services/BSMessage.service';
import { BSUtils } from './../../core/services/BSUtils.service';
import { ImagePage } from './../modal/image/image.page';
import { ModalFastTestComponent } from './modal/modal-fast-test';
import { RAJResource } from 'src/app/core/services/raj-model.services';
import { BSLoading } from 'src/app/core/services/BSLoading.service';
import { AppComponent } from 'src/app/app.component';
import { Pages } from 'src/app/interfaces/pages';
import { DomSanitizer } from '@angular/platform-browser';
import { BarcodeFormat, BarcodeScanner } from '@capacitor-mlkit/barcode-scanning';
import { Chart } from 'chart.js';

@Component({
  selector: 'app-home-results',
  templateUrl: './home-results.page.html',
  styleUrls: ['./home-results.page.scss']
})

export class HomeResultsPage implements OnInit, AfterViewInit {

  optionsCodeBar:any = {
    preferFrontCamera: false,
    showFlipCameraButton: false,
    showTorchButton: false,
    torchOn: false,
    prompt: 'Posicione o código de barras sob a linha vermelha e aguarde a leitura automática',
    resultDisplayDuration: 500,
    formats: 'QR_CODE,DATA_MATRIX,UPC_E,UPC_A,EAN_8,EAN_13,CODE_128,CODE_39,CODE_93,CODABAR,ITF,RSS14,RSS_EXPANDED,PDF_417,AZTEC,MSI',
    orientation: 'landscape',
  };

  urlCamera: boolean = null;
  urlScript: boolean = null;
  relatorio: boolean = false;
  ultimoSubMenu: boolean = null;
  fecharSubmenus: boolean = false;
  preparandoLeitor: boolean = false;
  preparandoRelatorio: boolean = false;
  validacaoBotoes: boolean = true;
  validacaoCards: boolean = true;
  validacaoGraficos: boolean = true;

  botoes = []
  parametro = []
  agrupadores = []
  resumoChamados = []
  utilizandoDados = []
  agrupadoresNovoServico = []
  lstCards = []
  lstGraficos = []

  contador: number = 0;
  totalChamados: number = 0;
  contadorSubmenu: number = 0;
  contadorCabecalho: number = 0;
  countDadosGrafico: number = 0;

  submenuAberto: any = null;

  titulo: string = '';
  myChart: string = '';
  btn_ativo: string = '';
  searchKey: string = '';
  mesFaturaAtual: string = '';
  titulo_cabecalho: string = '';
  yourLocation: string = '123 Test Street';
  themeCover: string = 'assets/img/ionic4-Start-Theme-cover.jpg';

  padding: number = 10;
  exibe_titulo: boolean = true;
  exibir_legenda: boolean = true;
  posicao_legenda: any = 'bottom';
  exibir_eixo_x: boolean = true;
  exibir_eixo_y: boolean = true;
  exibir_grade: boolean = true;

  usuarioAutenticado = { nome: '', plano: '', login: '' }
  ultimaFatura = { valor: '', status: '', dataVencimento: '' }
  listSubMenus = { subMenus: [], submenuVisible: false, rotaUnica: null }

  public appPages: Array<Pages> = [];

  diretorHomeCards = {
    "result": [{
      "Falhas": "",
      "lstCards": [
        { "titulo": "Recebimentos", "subtitulo": "", "label1": "label 1", "label2": "", "label3": "", "footer": "", "bgColor": "#fff", "borderColor": "#b7b7b7", "fontColor": "#939393" },
        { "titulo": "Pagamentos", "subtitulo": "", "label1": "label 1", "label2": "", "label3": "", "footer": "", "bgColor": "#fff", "borderColor": "#b7b7b7", "fontColor": "#939393" },
        { "titulo": "Faturamento Previsto", "subtitulo": "", "label1": "label 1", "label2": "", "label3": "", "footer": "", "bgColor": "#fff", "borderColor": "#b7b7b7", "fontColor": "#939393" },
        { "titulo": "Compras Prevista", "subtitulo": "", "label1": "label 1", "label2": "", "label3": "", "footer": "", "bgColor": "#fff", "borderColor": "#b7b7b7", "fontColor": "#939393" },
      ]
    }]
  }

  diretorHomeGraficos = {
    "result": [{
      "Falhas": "",
      "lstGraficos": [
        { "tipo": "horizontalBar", "titulo": "Titulo", "legenda": "legenda", "eixoX": ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho"], "eixoY": [1, 2, 3, 4, 5, 6], },
        { "tipo": "pie", "titulo": "Titulo", "legenda": "legenda", "eixoX": ["Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"], "eixoY": [7, 8, 9, 10, 11, 12] },
        { "tipo": "bar", "titulo": "Titulo", "legenda": "legenda", "eixoX": ["Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"], "eixoY": [3, 4, 6, 3, 5, 7] }
      ]
    }]
  }

  constructor(
    private bsAuth: BSAuth,
    private bsMessage: BSMessage,
    private bsLoading: BSLoading,
    public navCtrl: NavController,
    public menuCtrl: MenuController,
    private rajResource: RAJResource,
    public alertCtrl: AlertController,
    public modalCtrl: ModalController,
    public toastCtrl: ToastController,
    private domSanitizer: DomSanitizer,
    private appComponente: AppComponent,
    public popoverCtrl: PopoverController
  ) {
    this.usuarioAutenticado = this.bsAuth.usuario
    this.appPages = []
  }

  ngOnInit(): void {
  }

  ngAfterViewInit() {
    this.carregarBotoes();
    this.carregarCards();
    this.carregarGraficos();
  }

  async carregarCards() {
    try {
      let data = await this.rajResource.dadosCards().toPromise()
      this.lstCards = data['result'][0]['lstCards']
      this.titulo = this.lstCards[0]['titulo']
    } catch (error) {
      console.warn(error)
    }
  }

  async carregarGraficos() {
    try {
      let data = await this.rajResource.dadosGraficos().toPromise()
      this.lstGraficos = data['result'][0]['lstGraficos']
      this.exemploGrafico()
    } catch (error) {
      console.warn(error)
      this.rajResource.logSistema(error['status'], error['statusText']+" - "+error['name'], error['url'])
    }

  }

  async refresToken() {
    setTimeout(() => {
      let tokenExpired = this.bsAuth.isTokenExpired();
      if (tokenExpired) {
        console.log('Token expirado')
        this.bsAuth.refreshToken()
      } else {
        console.log('Token nao expirado')
        this.refresToken()
      }
    }, 5000)
  }

  carregarDados() {
    this.rajResource.getGrupoRelatorios()
      .subscribe(
        data => {
          console.log('Montando Home')
          this.agrupadores = data['result'][0]['Botoes']
          let qtdGrupos = 1
          for (let item of this.agrupadores) {
            if (qtdGrupos <= 6) {
              item['visible'] = true
              item['style'] = "background-color: #B0E2FF; height: 145px;"
              item['contemImg'] = true
              item['mostraCaption'] = false
              //item['ImgB64'] = undefined
              if (item['ImgB64'] == undefined) {
                item['img'] = "/assets/img/button_base_2.png"
                item['mostraCaption'] = true
              } else {
                item['mostraCaption'] = false
                item['img'] = "data:image/png;base64," + item['ImgB64'].replace(/(\r\n|\n|\r)/gm, "")
              }
              //item['rota'] = '/' + item['Parametro'] + '/'
              item['rota'] = '/relatorios/' + `{"agrupador":"${String(item['Caption'])}","caption":" ${item['Caption']}"}`;
              qtdGrupos = qtdGrupos + 1
            } else {
              item['visible'] = false
            }
          }

          this.appComponente.agrupadores = this.agrupadores

          //dados do Menu
          let response = data['result'][0]['Botoes']
          for (let item of response) {
            this.appPages.push(
              {
                title: item['Caption'],
                url: '/relatorios/' + `{"agrupador":"${String(item['Caption'])}","caption":" ${item['Caption']}"}`,
                direct: 'forward',
                icon: 'albums-outline'
              }
            )
          }

          this.appComponente.appPagesRecursos = this.appPages
        }
      );
  }

  async carregarBotoes() {
    try {
      let data = await this.rajResource.get_diretorListarTabs().toPromise()
      this.agrupadoresNovoServico = data['result'][0]['Botoes']

      let logoRight = data['result'][0]['logoFooterRight']
      let logoLeft = data['result'][0]['logoFooterLeft']

      if (logoRight != "") {
        this.appComponente.logoRight = "data:image/png;base64," + logoRight.replace(/(\r\n|\n|\r)/gm, "")
      } else if (logoLeft != "") {
        this.appComponente.logoLeft = "data:image/png;base64," + logoLeft.replace(/(\r\n|\n|\r)/gm, "")
      }

      for (let item of this.agrupadoresNovoServico) {

        for (let subMenus of item['subMenus']) {
          if (subMenus['scp'] != "") {
            subMenus['rotaFinal'] = subMenus['scp']
          } else if (subMenus['rota'] != "") {
            subMenus['rotaFinal'] = subMenus['rota']
            subMenus['LerCodBarra'] = "S"
          } else if (subMenus['rota'] != "" && subMenus['scp'] != "") {
            subMenus['rotaFinal'] = ""
            subMenus['LerCodBarra'] = "S"
          } else {
            subMenus['rotaFinal'] = null
            item['existeRota'] = "N"
          }

          let retorno = this.hasMultipleElements(item, 'subMenus')
          if (retorno == false) {
            item['rotaUnica'] = subMenus['rotaFinal']
          } else {
            item['rotaUnica'] = "Não é rota única!"
          }
        }
      }

    } catch (error) {
      console.warn(error)
    }
  }

  botoesRotaUnica(p) {
    this.rajResource.titulo_cabecalho = '';

    if (p['rotaUnica'] != "") {
      if (this.btn_ativo != p['label']) {
        this.contador = 0;
      }
      this.contador += 1;
      this.btn_ativo = p['label'];
      this.rajResource.titulo_cabecalho = p['label'];
      console.log(this.rajResource.titulo_cabecalho = p['label'])
      this.contadorSubmenu = this.listSubMenus.subMenus.length;

      let fundo = document.querySelector('.div-dashboard') as HTMLElement

      if (this.contador == 2) {
        this.contador = 0;
        this.btn_ativo = "";
        this.listSubMenus.submenuVisible = false;
        fundo.style.background = 'transparent';
      } else if (this.contadorSubmenu == 1) {
        this.contador = 0;
        this.listSubMenus.submenuVisible = false;
        fundo.style.background = 'transparent';
      } else {
        this.listSubMenus.submenuVisible = true;
        fundo.style.background = 'rgba(0, 0, 0, 0.2)';
      }

      if (p['rotaUnica'] == "Não é rota única!") {
      } else if (p['rotaUnica'] == "APP_LER_CODBARRA") {
        this.iniciarCamera()
      } else if (p['rotaUnica'] != "APP_LER_CODBARRA") {
        this.processaHTML(p['rotaUnica'])
      }
    }
  }

  redirecionarRota(p) {
    if (this.contadorSubmenu != 1) {
      this.rajResource.titulo_cabecalho += " - " + p['label'];
    }
    if (p['LerCodBarra'] == 'S') {
      this.iniciarCamera()
    }
    if (p['scp'] != "") {
      this.listSubMenus.submenuVisible = false;
      this.processaHTML(p['scp'])
    }
    this.contador = 0;
    this.btn_ativo = "";
    let fundo = document.querySelector('.div-dashboard') as HTMLElement
    fundo.style.background = 'transparent';
    this.listSubMenus.submenuVisible = false;
  }

  showSubMenu(event: MouseEvent, p) {
    this.rajResource.titulo_cabecalho = p['label'];
    if (this.submenuAberto !== null && this.submenuAberto !== p) {
      this.submenuAberto.submenuVisible = false;
    }
    this.fecharSubmenus = false
    if (p.subMenus.length > 1) {
      p.submenuVisible = true;
    }

    this.contadorSubmenu = this.listSubMenus.subMenus.length;
    this.btn_ativo = p['label'];
    let fundo = document.querySelector('.div-dashboard') as HTMLElement
    fundo.style.background = this.contadorSubmenu > 1 ? 'rgba(0, 0, 0, 0.2)' : 'transparent';
    this.submenuAberto = p;
    this.subMenus(p)
    this.ultimoSubMenu = this.guardaUltimoMenuSelecionado(p)
  }

  hideSubMenu(event: MouseEvent, p) {
    p.submenuVisible = false;
  }

  processaHTML(script) {
    this.rajResource.urlScript = script;
    this.rajResource.tipo_relatorio = 'script';
    this.navCtrl.navigateForward('/relatorio')
  }

  goToHome() {
    this.relatorio = false;
    this.preparandoRelatorio = false;
    this.listSubMenus.submenuVisible = false;
    this.btn_ativo = '';
    setTimeout(() => {
      this.exemploGrafico();
    }, 1000)
  }

  hasMultipleElements(obj: any, prop: string): boolean {
    if (!obj || !obj[prop]) {
      return false;
    }
    const arr = Array.isArray(obj[prop]) ? obj[prop] : [obj[prop]];
    return arr.length > 1;
  }

  ionViewWillEnter() {
    this.menuCtrl.enable(true);
  }

  settings() {
    this.navCtrl.navigateForward('settings');
  }

  async presentImage(image: any) {
    const modal = await this.modalCtrl.create({
      component: ImagePage,
      componentProps: { value: image }
    });
    return await modal.present();
  }

  async notifications(ev: any) {
    const popover = await this.popoverCtrl.create({
      component: NotificationsComponent,
      event: ev,
      animated: true,
      showBackdrop: true
    });
    return await popover.present();
  }

  async logout() {
    this.bsMessage.confirma('Deseja sair da aplicação?', () => {
      this.bsAuth.logout()
    });
  }

  getTotalChamados() {
    for (let c of this.resumoChamados) {
      this.totalChamados = this.totalChamados + c.quantidade
    }
  }

  doRefresh(event) {
    setTimeout(() => {
      this.carregarDados()
      event.target.complete();
    }, 10000);
  }

  atualizarToken() {
    alert('Token expirado:' + this.bsAuth.isTokenExpired())
  }

  goToSuporte() {
    this.appComponente.goToSuporte()
  }

  guardaUltimoMenuSelecionado(p) {
    return this.ultimoSubMenu = p;
  }

  desfocarSubMenu() {
    this.fecharSubmenus = true
    // let ultimoSubMenu = this.ultimoSubMenu
    // ultimoSubMenu.submenuVisible = false
    // this.submenuAberto = ultimoSubMenu
    // console.log(ultimoSubMenu)
  }

  subMenus(p) {
    let dados = p
    this.listSubMenus.subMenus = dados['subMenus']
    this.listSubMenus.submenuVisible = dados['submenuVisible']
    this.listSubMenus.rotaUnica = dados['rotaUnica']
  }

  async iniciarCamera() {
  try {
    this.preparandoLeitor = true;

    // Solicita permissão
    const { camera } = await BarcodeScanner.requestPermissions();
    if (camera !== 'granted') {
      this.preparandoLeitor = false;
      this.bsMessage.informa ('Permissão da câmera negada.');
      return;
    }

    // Inicia a leitura
   const { barcodes } = await BarcodeScanner.scan({
    formats: [BarcodeFormat.QrCode, BarcodeFormat.Ean13],
  });

    this.preparandoLeitor = false;

    if (!barcodes || barcodes.length === 0) {
      return;
    }

    // Pega o primeiro código detectado
    const codigoBarras = barcodes[0]?.rawValue;

    if (codigoBarras && codigoBarras.length > 0) {
      this.lerCodigoBarras(codigoBarras);
    }

  } catch (err) {
    this.preparandoLeitor = false;
    this.bsMessage.error('Erro ao realizar leitura: ' + err);
  }
}


  lerCodigoBarras(codigoBarras) {
    this.preparandoRelatorio = false
    console.log('Lendo codigo barras:' + codigoBarras)
    console.log('Lendo codigo barras:' + btoa(codigoBarras))
    this.bsLoading.loading()
    this.rajResource.urlCamera = codigoBarras;
    this.rajResource.titulo_cabecalho = 'Cod. Barras';
    this.rajResource.tipo_relatorio = 'camera';
    this.navCtrl.navigateForward('/relatorio')
  }

  exemploGrafico() {
    const graficoDiv = document.querySelector('.grafico');
    graficoDiv.innerHTML = '';

    for (let i = 0; i < this.lstGraficos.length; i++) {
      const chartId = `myChart${i}`;
      const canvas = document.createElement('canvas');
      canvas.id = chartId;
      graficoDiv.appendChild(canvas);

      let getCanvas = document.querySelector('#' + chartId) as HTMLLIElement;
      getCanvas.style.cssText = 'width: 100%; max-height: 40vh; border-radius: 5px; border: 1px solid rgba(0, 0, 0, 0.4);';

      let titulo = this.lstGraficos[i]['titulo'];

      this.exibe_titulo = titulo !== '' ? true : false;

      if (this.lstGraficos[i]['tipo'] == 'pie') {
        this.padding = 10;
        this.exibir_eixo_x = false;
        this.exibir_eixo_y = false;
        this.exibir_legenda = true;
        this.posicao_legenda = 'right';
      }

      else if (this.lstGraficos[i]['tipo'] == 'bar') {
        this.padding = 10;
        this.exibir_eixo_x = true;
        this.exibir_eixo_y = true;
        this.exibir_legenda = false
        this.posicao_legenda = 'bottom';
        this.exibir_grade = true;
      }

      else if (this.lstGraficos[i]['tipo'] == 'horizontalBar') {
        this.padding = 10;
        this.exibir_eixo_x = true;
        this.exibir_eixo_y = true;
        this.exibir_legenda = false
        this.posicao_legenda = 'bottom';
        this.exibir_grade = false;
        getCanvas.style.paddingRight = '10px';
      }

      new Chart(chartId, {
        type: this.lstGraficos[i]['tipo'],
        data: {
          labels: this.lstGraficos[i]['eixoX'],
          datasets: [{
            label: this.lstGraficos[i]['legenda'],
            data: this.lstGraficos[i]['eixoY'],
            backgroundColor: [
              'rgba(255, 128, 0, 1)',
              'rgba(255, 0, 55, 1)',
              'rgba(24, 161, 0, 1)',
              'rgba(255, 183, 0, 1)',
              'rgba(0, 51, 161, 1)',
              'rgba(180, 60, 255, 1)',
            ],
            borderColor: [
              'rgba(255, 128, 0, 1)',
              'rgba(255, 0, 55, 1)',
              'rgba(24, 161, 0, 1)',
              'rgba(255, 183, 0, 1)',
              'rgba(0, 51, 161, 1)',
              'rgba(180, 60, 255, 1)',
            ],
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,

          title: {
            display: this.exibe_titulo,
            position: 'top',
            fontSize: 22,
            text: titulo,
          },

          legend: {
            display: this.exibir_legenda,
            position: this.posicao_legenda,
          },

          layout: {
            padding: {
              left: this.padding,
              right: this.padding,
              top: this.padding,
              bottom: this.padding
            }
          },

          scales: {
            yAxes: [{
              ticks: {
                beginAtZero: true,
                display: this.exibir_eixo_y,
              },
              gridLines: {
                display: this.exibir_grade,
              },
            }],
            xAxes: [{
              ticks: {
                beginAtZero: true,
                display: this.exibir_eixo_x,
                callback: function (value: any, index, values) {
                  return value.toLocaleString('pt-BR', { minimumFractionDigits: 2 })
                }
              },
              gridLines: {
                display: this.exibir_grade,
              }
            }]
          },

        }
      });
    }
  }

}