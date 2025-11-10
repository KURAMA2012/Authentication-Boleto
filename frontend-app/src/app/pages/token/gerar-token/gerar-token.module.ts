import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { GerarTokenPageRoutingModule } from './gerar-token-routing.module';

import { GerarTokenPage } from './gerar-token.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    GerarTokenPageRoutingModule
  ],
  declarations: [GerarTokenPage]
})
export class GerarTokenPageModule {}
