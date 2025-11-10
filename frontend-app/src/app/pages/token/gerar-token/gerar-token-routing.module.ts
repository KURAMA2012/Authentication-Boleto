import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { GerarTokenPage } from './gerar-token.page';

const routes: Routes = [
  {
    path: '',
    component: GerarTokenPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class GerarTokenPageRoutingModule {}
