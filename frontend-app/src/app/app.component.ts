import { Component } from '@angular/core';
import { MenuController } from '@ionic/angular';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from './core/services/AuthService';

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
  usuarioNome = 'Empresa';

  /** Rotas publicas: sem menu/split-pane. */
  private readonly rotasPublicas = ['/inicio', '/validar', '/login', '/register'];

  constructor(private menuCtrl: MenuController, private router: Router, private auth: AuthService) {
    this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe((e) => {
        const url = (e.urlAfterRedirects || e.url || '').split('?')[0];
        this.telaPublica = url === '/' || this.rotasPublicas.some((r) => url.startsWith(r));
        this.usuarioNome = this.auth.nomeExibicao();
        this.menuCtrl.enable(!this.telaPublica, 'main-menu');
      });
  }

  sair() {
    this.auth.logout();
  }

  openMenu() {
    this.menuCtrl.open('main-menu');
  }

  goToSuporte() {
    window.open('https://seusite.com/suporte', '_blank');
  }
}
