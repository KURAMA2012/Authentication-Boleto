import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Routes, RouterModule } from '@angular/router';

import { IonicModule } from '@ionic/angular';

import { SuporteDownloadsPage } from './suporte-downloads.page';

const routes: Routes = [
  {
    path: '',
    component: SuporteDownloadsPage
  }
];

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    RouterModule.forChild(routes)
  ],
  declarations: [SuporteDownloadsPage]
})
export class SuporteDownloadsPageModule {}
