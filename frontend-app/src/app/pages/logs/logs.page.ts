import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ToastController } from '@ionic/angular';

interface BoletoLog {
  id: number;
  codigoAutenticacao: string;
  ipUsuario: string;
  valido: boolean;
  mensagem: string;
  dataConsulta: string;
  boleto?: any;
}

@Component({
  selector: 'app-logs',
  templateUrl: './logs.page.html',
  styleUrls: ['./logs.page.scss'],
})
export class LogsPage implements OnInit {

  logs: BoletoLog[] = [];
  todosLogs: BoletoLog[] = [];
  loading: boolean = false;
  searchTerm: string = '';
  page: number = 1;
  pageSize: number = 10;

  constructor(private http: HttpClient, private toastCtrl: ToastController) {}

  ngOnInit() {
    this.carregarLogs();
  }

  async carregarLogs(event?: any) {
    if (this.page === 1) this.loading = true;

    this.http.get<BoletoLog[]>('http://localhost:8080/api/logs')
      .subscribe({
        next: (data) => {
          this.todosLogs = data;
          this.aplicarFiltro();

          this.loading = false;
          if (event) event.target.complete();
        },
        error: async () => {
          this.loading = false;
          if (event) event.target.complete();

          const toast = await this.toastCtrl.create({
            message: 'Erro ao carregar logs!',
            duration: 2500,
            color: 'danger'
          });
          toast.present();
        }
      });
  }

  aplicarFiltro() {
    const termo = this.searchTerm.trim().toLowerCase();
    let filtrados = this.todosLogs;

    if (termo) {
      filtrados = this.todosLogs.filter(log =>
        log.codigoAutenticacao.toLowerCase().includes(termo) ||
        log.ipUsuario.toLowerCase().includes(termo) ||
        log.mensagem.toLowerCase().includes(termo)
      );
    }

    const inicio = 0;
    const fim = this.page * this.pageSize;
    this.logs = filtrados.slice(inicio, fim);
  }

  carregarMais(event: any) {
    this.page++;
    this.aplicarFiltro();
    event.target.complete();

    if (this.logs.length >= this.todosLogs.length) {
      event.target.disabled = true;
    }
  }

  buscar() {
    this.page = 1;
    this.aplicarFiltro();
  }

  formatarData(data: string): string {
    return new Date(data).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'medium' });
  }

  get corStatus() {
    return (valido: boolean) => valido ? 'success' : 'danger';
  }
}
