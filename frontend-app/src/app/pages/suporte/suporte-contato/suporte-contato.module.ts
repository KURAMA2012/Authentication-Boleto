import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Routes, RouterModule } from '@angular/router';

import { IonicModule } from '@ionic/angular';

import { SuporteContatoPage } from './suporte-contato.page';

const routes: Routes = [
  {
    path: '',
    component: SuporteContatoPage
  }
];

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    RouterModule.forChild(routes)
  ],
  declarations: [SuporteContatoPage]
})
export class SuporteContatoPageModule {}
