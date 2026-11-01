import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { ValidarPage } from './validar.page';

const routes: Routes = [{ path: '', component: ValidarPage }];

@NgModule({
  imports: [CommonModule, ReactiveFormsModule, IonicModule, RouterModule.forChild(routes)],
  declarations: [ValidarPage],
})
export class ValidarPageModule {}