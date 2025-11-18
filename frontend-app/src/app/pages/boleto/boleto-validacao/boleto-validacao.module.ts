import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';

import { BoletoValidacaoPageRoutingModule } from './boleto-validacao-routing.module';
import { BoletoValidacaoPage } from './boleto-validacao.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    BoletoValidacaoPageRoutingModule
  ],
  declarations: [BoletoValidacaoPage]
})
export class BoletoValidacaoPageModule {}
