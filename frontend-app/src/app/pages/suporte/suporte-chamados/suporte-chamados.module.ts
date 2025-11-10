import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Routes, RouterModule } from '@angular/router';

import { IonicModule } from '@ionic/angular';

import { SuporteChamadosPage } from './suporte-chamados.page';

const routes: Routes = [
  {
    path: '',
    component: SuporteChamadosPage
  }
];

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    RouterModule.forChild(routes)
  ],
  declarations: [SuporteChamadosPage]
})
export class SuporteChamadosPageModule {}
