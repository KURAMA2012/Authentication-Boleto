import { Component } from '@angular/core';
import { MenuController } from '@ionic/angular';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent {

  agrupadores: any;
  appPagesRecursos: any;
  logoRight: string;
  logoLeft: string;

  constructor(private menuCtrl: MenuController) {}

  openMenu() {
    this.menuCtrl.open('main-menu');
  }

  goToSuporte() {
    window.open('https://seusite.com/suporte', '_blank');
  }
}
