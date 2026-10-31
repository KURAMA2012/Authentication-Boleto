import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FeedbackService } from '../../shared/feedback.service';
import { environment } from '../../../environments/environment';

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
  erro = false;
  skeletons = [1, 2, 3, 4, 5];

  get totalValidos(): number {
    return this.todosLogs.filter(l => l.valido).length;
  }

  recarregar(event?: any) {
    this.page = 1;
    this.carregarLogs(event);
  }

  constructor(private http: HttpClient, private feedback: FeedbackService) {}

  ngOnInit() {
    this.carregarLogs();
  }

  async carregarLogs(event?: any) {
    if (this.page === 1) this.loading = true;
    this.erro = false;

    this.http.get<BoletoLog[]>(`${environment.apiBaseUrl}/api/logs`)
      .subscribe({
        next: (data) => {
          this.todosLogs = data || [];
          this.aplicarFiltro();

          this.loading = false;
          if (event) event.target.complete();
        },
        error: async (err) => {
          this.loading = false;
          this.erro = true;
          if (event) event.target.complete();

          await this.feedback.erro(err, 'Erro ao carregar logs.');
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
