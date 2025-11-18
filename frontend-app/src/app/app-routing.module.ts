import { NgModule } from '@angular/core';
import { Routes, RouterModule, PreloadAllModules } from '@angular/router';
import { AuthGuard } from './core/services/AuthGuard.services';

const routes: Routes = [

  { path: '', redirectTo: 'login', pathMatch: 'full' },

  { path: 'register', loadChildren: () => import('./pages/register/register.module').then(m => m.RegisterPageModule) },

  { path: 'login', loadChildren: () => import('./pages/login/login.module').then(m => m.LoginPageModule) },

  {
    path: 'cadastro-boleto',
    loadChildren: () => import('./pages/boleto/cadastro-boleto/cadastro-boleto.module').then(m => m.CadastroBoletoPageModule),
    canActivate: [AuthGuard]
  },

 //{ path: 'boleto/validacao', loadChildren: () => import('./pages/boleto/boleto-validacao/boleto-validacao.module').then(m => m.BoletoValidacaoPageModule) },

 { path: 'boleto-validacao',  loadChildren: () => import('./pages/boleto/boleto-validacao/boleto-validacao.module').then( m => m.BoletoValidacaoPageModule)},


  {
    path: 'cadastro-empresa',
    loadChildren: () => import('./pages/cadastro-empresa/cadastro-empresa.module').then(m => m.CadastroEmpresaPageModule),
    canActivate: [AuthGuard]
  },

  {
    path: 'home',
    loadChildren: () => import('./pages/home/home.module').then(m => m.HomePageModule),
    canActivate: [AuthGuard]
  },

  {
    path: 'logs',
    loadChildren: () => import('./pages/logs/logs.module').then(m => m.LogsPageModule),
    canActivate: [AuthGuard]
  },

];

@NgModule({
  imports: [RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
