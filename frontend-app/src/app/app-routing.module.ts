import { NgModule } from '@angular/core';
import { inject } from '@angular/core';
import { Routes, RouterModule, PreloadAllModules, Router } from '@angular/router';
import { AuthGuard } from './core/services/AuthGuard.services';
import { AuthService } from './core/services/AuthService';

/** Quem ja esta logado nao ve a home publica: vai direto para o painel. */
const logadoVaiParaPainel = () => (inject(AuthService).isLogged() ? inject(Router).parseUrl('/painel') : true);

const routes: Routes = [
  { path: '', redirectTo: 'inicio', pathMatch: 'full' },

  // Publicas (sem AuthGuard e sem menu)
  {
    path: 'inicio',
    loadChildren: () => import('./pages/inicio/inicio.module').then((m) => m.InicioPageModule),
    canActivate: [logadoVaiParaPainel],
  },
  {
    path: 'validar',
    loadChildren: () => import('./pages/validar/validar.module').then((m) => m.ValidarPageModule),
  },

  {
    path: 'register',
    loadChildren: () =>
      import('./pages/register/register.module').then((m) => m.RegisterPageModule),
  },
  {
    path: 'login',
    loadChildren: () =>
      import('./pages/login/login.module').then((m) => m.LoginPageModule),
  },
  {
    path: 'cadastro-boleto',
    loadChildren: () =>
      import('./pages/boleto/cadastro-boleto/cadastro-boleto.module').then(
        (m) => m.CadastroBoletoPageModule
      ),
    canActivate: [AuthGuard],
  },
  {
    path: 'boleto-validacao',
    loadChildren: () =>
      import('./pages/boleto/boleto-validacao/boleto-validacao.module').then(
        (m) => m.BoletoValidacaoPageModule
      ),
  },
  {
    path: 'cadastro-empresa',
    loadChildren: () =>
      import('./pages/cadastro-empresa/cadastro-empresa.module').then(
        (m) => m.CadastroEmpresaPageModule
      ),
    canActivate: [AuthGuard],
  },
  {
    path: 'painel',
    loadChildren: () => import('./pages/painel/painel.module').then((m) => m.PainelPageModule),
    canActivate: [AuthGuard],
  },
  { path: 'home', redirectTo: 'painel', pathMatch: 'full' },
  {
    path: 'logs',
    loadChildren: () =>
      import('./pages/logs/logs.module').then((m) => m.LogsPageModule),
    canActivate: [AuthGuard],
  },
  { path: '**', redirectTo: 'inicio' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
