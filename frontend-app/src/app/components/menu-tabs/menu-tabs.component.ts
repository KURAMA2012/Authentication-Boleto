import { Component, OnInit } from '@angular/core';
import { NavController } from '@ionic/angular';


@Component({
  selector: 'menu-tabs',
  templateUrl: './menu-tabs.component.html',
  styleUrls: ['./menu-tabs.component.scss']
})
export class MenuTabsComponent implements OnInit {

  tab = 'calendar';

  show(tab) {
    this.tab = tab;
  }

  constructor(  public navCtrl: NavController) { }

  ngOnInit() {
  }


  goToHome() {
    this.navCtrl.navigateForward('home-results');
  }

  goToFinanceiro() {
    this.navCtrl.navigateForward('financeiro');
  }

  goToSuporte() {
    this.navCtrl.navigateForward('suporte');
  }

  goToMinhaConta() {
    this.navCtrl.navigateForward('edit-profile');
  }

}
