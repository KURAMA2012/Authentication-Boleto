import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { ConfirmarTokenPageRoutingModule } from './confirmar-token-routing.module';

import { ConfirmarTokenPage } from './confirmar-token.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    ConfirmarTokenPageRoutingModule
  ],
  declarations: [ConfirmarTokenPage]
})
export class ConfirmarTokenPageModule {}
