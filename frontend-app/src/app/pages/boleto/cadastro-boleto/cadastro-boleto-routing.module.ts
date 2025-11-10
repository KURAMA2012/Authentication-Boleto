import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { CadastroBoletoPage } from './cadastro-boleto.page';

const routes: Routes = [
  {
    path: '',
    component: CadastroBoletoPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CadastroBoletoPageRoutingModule {}
