import { Component } from '@angular/core';
import { MenuController } from '@ionic/angular';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';

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

  /** Estado de UI: esconde o menu/split-pane nas telas de login e cadastro */
  telaPublica = true;

  constructor(private menuCtrl: MenuController, private router: Router) {
    this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe((e) => {
        const url = (e.urlAfterRedirects || e.url || '').split('?')[0];
        this.telaPublica = url === '/' || url.startsWith('/login') || url.startsWith('/register');
        this.menuCtrl.enable(!this.telaPublica, 'main-menu');
      });
  }

  openMenu() {
    this.menuCtrl.open('main-menu');
  }

  goToSuporte() {
    window.open('https://seusite.com/suporte', '_blank');
  }
}
