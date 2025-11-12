import { NgModule } from '@angular/core';
import { Routes, RouterModule, PreloadAllModules } from '@angular/router';

const routes: Routes = [

  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', loadChildren: () => import('./pages/login/login.module').then(m => m.LoginPageModule) },

  { path: '', redirectTo: 'boleto/list', pathMatch: 'full' },

  { path: 'boleto/validacao', loadChildren: () => import('./pages/boleto/boleto-validacao/boleto-validacao.module').then(m => m.BoletoValidacaoPageModule) },

  {
    path: 'boleto-validacao',
    loadChildren: () => import('./pages/boleto/boleto-validacao/boleto-validacao.module').then( m => m.BoletoValidacaoPageModule)
  },
  {
    path: 'cadastro-boleto',
    loadChildren: () => import('./pages/boleto/cadastro-boleto/cadastro-boleto.module').then( m => m.CadastroBoletoPageModule)
  },
  {
    path: 'gerar-token',
    loadChildren: () => import('./pages/token/gerar-token/gerar-token.module').then( m => m.GerarTokenPageModule)
  },
  {
    path: 'confirmar-token',
    loadChildren: () => import('./pages/token/confirmar-token/confirmar-token.module').then( m => m.ConfirmarTokenPageModule)
  },
  {
    path: 'home',
    loadChildren: () => import('./pages/home/home.module').then( m => m.HomePageModule)
  },
  {
    path: 'logs',
    loadChildren: () => import('./pages/logs/logs.module').then( m => m.LogsPageModule)
  },
  {
    path: 'cadastro-empresa',
    loadChildren: () => import('./pages/cadastro-empresa/cadastro-empresa.module').then( m => m.CadastroEmpresaPageModule)
  },

];

@NgModule({
  imports: [RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })],
  exports: [RouterModule]
})
export class AppRoutingModule {}