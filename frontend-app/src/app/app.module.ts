import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouteReuseStrategy } from '@angular/router';

import { IonicModule, IonicRouteStrategy } from '@ionic/angular';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';

import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';


// === COMPONENTES DO PROJETO ===

// === SERVICES DO PROJETO ===
import { AuthService } from './core/services/AuthService';
import { BoletoService } from './core/services/BoletoService.services';
import { EmpresaService } from './core/services/EmpresaService.services';
import { AuthGuard } from './core/services/AuthGuard.services';
import { BSMessage } from './core/services/BSMessage.service';
import { BSLoading } from './core/services/BSLoading.service';
import { BSUtils } from './core/services/BSUtils.service';
import { LogService } from './core/services/LogService.services';
import { TokenService } from './core/services/TokenService.services';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
  BrowserModule,
  IonicModule.forRoot(),
  AppRoutingModule,
  HttpClientModule,
  FormsModule,
  ReactiveFormsModule
],
  providers: [
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },

    // SERVICES
  AuthService,
  AuthGuard,
  BoletoService,
  EmpresaService,

  BSMessage,
  BSLoading,
  BSUtils,
  LogService,
  TokenService
  ],
  bootstrap: [AppComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppModule {}

