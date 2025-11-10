import { BSAuth } from 'src/app/core/services/BSAuth.service';
import { BSLoading } from '../../../core/services/BSLoading.service';
import { Component, Input } from "@angular/core";
import { ModalController, Platform } from "@ionic/angular";

import { Filesystem, Directory, Encoding } from '@capacitor/filesystem';
import { Browser } from '@capacitor/browser';
import { HttpClient } from '@angular/common/http';

@Component({
  templateUrl: './visualizar-boleto.html',
  styleUrls: ['./visualizar-boleto.scss']
})
export class VisualizarBoletoComponent {
  urlBoleto: any = '';

  @Input() titulo: string;
  @Input() fatura: any;

  constructor(
    private modalCtrl: ModalController,
    private bsLoading: BSLoading,
    private bsAuth: BSAuth,
    private platform: Platform,
    private http: HttpClient
  ) {
    this.openRemoteFileByUrl();
  }

  fecharModal() {
    this.modalCtrl.dismiss();
  }

  async openRemoteFileByUrl() {
    try {
      this.bsLoading.loading()
      
      const fileUrl = this.urlBoleto || ''; // URL do PDF
      const response = await this.http.get(fileUrl, { responseType: 'blob' }).toPromise();

      // Salvar arquivo localmente
      const base64Data = await this.convertBlobToBase64(response) as string;

      const fileName = 'boleto.pdf';
      const savedFile = await Filesystem.writeFile({
        path: fileName,
        data: base64Data,
        directory: Directory.Documents,
        encoding: Encoding.UTF8
      });

      // Abrir o PDF com o navegador do dispositivo
      await Browser.open({ url: savedFile.uri });

    } catch (err) {
      console.error('Erro ao abrir PDF:', err);
      this.bsLoading.unloading()
    } finally {
      this.bsLoading.unloading();
    }
  }

  private convertBlobToBase64(blob: Blob): Promise<string | ArrayBuffer> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onerror = reject;
      reader.onload = () => resolve(reader.result);
      reader.readAsDataURL(blob);
    });
  }
}
