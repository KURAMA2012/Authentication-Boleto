import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { CadastroBoletoPageRoutingModule } from './cadastro-boleto-routing.module';

import { CadastroBoletoPage } from './cadastro-boleto.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    CadastroBoletoPageRoutingModule
  ],
  declarations: [CadastroBoletoPage]
})
export class CadastroBoletoPageModule {}
