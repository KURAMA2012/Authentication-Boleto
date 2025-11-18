import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BoletoValidacaoPage } from './boleto-validacao.page';

const routes: Routes = [
  {
    path: '',
    component: BoletoValidacaoPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BoletoValidacaoPageRoutingModule {}
