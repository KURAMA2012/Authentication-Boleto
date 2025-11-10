import { NgModule } from '@angular/core';
import { Routes, RouterModule, PreloadAllModules } from '@angular/router';

const routes: Routes = [
  { path: '', redirectTo: 'boleto/list', pathMatch: 'full' },

  //{ path: 'empresa/list', loadChildren: () => import('./pages/empresa/empresa-list/empresa-list.module').then(m => m.EmpresaListPageModule) },
  //{ path: 'empresa/form', loadChildren: () => import('./pages/empresa/empresa-form/empresa-form.module').then(m => m.EmpresaFormPageModule) },

  //{ path: 'boleto/list', loadChildren: () => import('./pages/boleto/boleto-list/boleto-list.module').then(m => m.BoletoListPageModule) },
  //{ path: 'boleto/detalhe/:id', loadChildren: () => import('./pages/boleto/boleto-detalhe/boleto-detalhe.module').then(m => m.BoletoDetalhePageModule) },
  { path: 'boleto/validacao', loadChildren: () => import('./pages/boleto/boleto-validacao/boleto-validacao.module').then(m => m.BoletoValidacaoPageModule) },

  //{ path: 'token/validacao', loadChildren: () => import('./pages/token/token-validacao/token-validacao.module').then(m => m.TokenValidacaoPageModule) },

  //{ path: 'logs', loadChildren: () => import('./pages/logs/boleto-logs/boleto-logs.module').then(m => m.BoletoLogsPageModule) },
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
  },  {
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