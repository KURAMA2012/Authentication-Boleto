import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ConfirmarTokenPage } from './confirmar-token.page';

const routes: Routes = [
  {
    path: '',
    component: ConfirmarTokenPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ConfirmarTokenPageRoutingModule {}
