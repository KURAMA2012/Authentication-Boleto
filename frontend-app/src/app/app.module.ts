import {  registerLocaleData } from '@angular/common';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import br from '@angular/common/locales/pt';
import { CUSTOM_ELEMENTS_SCHEMA, ErrorHandler, LOCALE_ID, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouteReuseStrategy } from '@angular/router';

import { IonicModule, IonicRouteStrategy } from '@ionic/angular';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MenuTabsComponent } from './components/menu-tabs/menu-tabs.component';
import { ModalComponent } from './components/modal/modal.component';

import { NotificationsComponent } from './components/notifications/notifications.component';
import { ChamadoResource, ClienteResource, TituloResource } from './core/services/app-model.services';
import { BSAnaliseRelatorios } from './core/services/BSAnaliseRelatorios';
import { BSAppService } from './core/services/BSApp.services';
import { BSAuth } from './core/services/BSAuth.service';
import { AuthInterceptor } from './core/services/BSAuthInterceptor';
import { BSLoading } from './core/services/BSLoading.service';
import { BSMessage } from './core/services/BSMessage.service';
import { BSResource } from './core/services/BSResource.service';
import { BSUtils } from './core/services/BSUtils.service';
import { RAJResource } from './core/services/raj-model.services';
import { ModalFastTestComponent } from './pages/home-results/modal/modal-fast-test';

import { ImagePageModule } from './pages/modal/image/image.module';
import { DetalharFaturaComponent } from './pages/relatorios/modal/detalhar-fatura';
import { VisualizarBoletoComponent } from './pages/relatorios/modal/visualizar-boleto';
import { SuporteChatComponent } from './pages/suporte/modal/suporte-chat';

import { NgChartsModule } from 'ng2-charts';
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';


registerLocaleData(br, 'pt-BR');

@NgModule({
  declarations: [
    AppComponent,
    NotificationsComponent,
    ModalComponent,
    DetalharFaturaComponent,
    MenuTabsComponent,
    VisualizarBoletoComponent,
    SuporteChatComponent,
    ModalFastTestComponent,
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    BrowserModule,
    BrowserAnimationsModule,
    IonicModule.forRoot(),
    AppRoutingModule,
    HttpClientModule,
    NgChartsModule,
    ImagePageModule,
    NgxMaskDirective,
    NgxMaskPipe
  ],
  providers: [
    { provide: LOCALE_ID, useValue: 'pt-BR' },
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
    BSMessage,
    BSLoading,
    BSAuth,
    ClienteResource,
    BSResource,
    ChamadoResource,
    TituloResource,
    RAJResource,
    BSAnaliseRelatorios,
    BSAppService,
    BSUtils,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  bootstrap: [AppComponent]
})
export class AppModule {}
