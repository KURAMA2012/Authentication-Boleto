import { Component, OnInit } from '@angular/core';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { EmpresaService } from 'src/app/core/services/EmpresaService.services';
import { AuthService } from 'src/app/core/services/AuthService';
import { FeedbackService } from 'src/app/shared/feedback.service';

export interface EmpresaResumo { id: number; nome: string; cnpj: string; }
export interface MetricaDia { data: string; autenticas: number; suspeitas: number; }
export interface MetricasEmpresa {
  empresaId: number; de: string; ate: string;
  boletosEmitidos: number; validacoesAutenticas: number; validacoesSuspeitas: number;
  porDia: MetricaDia[];
}
/** Chave do localStorage com a ultima empresa escolhida no painel. */
export const CHAVE_EMPRESA_PAINEL = 'ab.painel.empresaId';

export interface BarraDia extends MetricaDia { total: number; pctAut: number; pctSus: number; rotulo: string; marcar: boolean; }

/** Painel de metricas da empresa do usuario logado. */
@Component({
  selector: 'app-painel',
  templateUrl: './painel.page.html',
  styleUrls: ['./painel.page.scss'],
})
export class PainelPage implements OnInit {
  readonly periodos = [7, 30, 90];
  periodo = 30;
  empresas: EmpresaResumo[] = [];
  empresaId: number | null = null;
  metricas: MetricasEmpresa | null = null;
  barras: BarraDia[] = [];
  maxDia = 0;

  carregandoEmpresas = true;
  carregandoMetricas = false;
  erro: string | null = null;

  constructor(private empresaSrv: EmpresaService, private auth: AuthService, private feedback: FeedbackService) {}

  get usuarioNome(): string { return this.auth.nomeExibicao(); }
  get semDados(): boolean { return !!this.metricas && this.barras.every((b) => b.total === 0); }

  ngOnInit() { this.carregarEmpresas(); }

  carregarEmpresas() {
    this.carregandoEmpresas = true;
    this.erro = null;
    this.empresaSrv.minhas().subscribe({
      next: (res: any) => {
        this.empresas = (res?.data ?? []) as EmpresaResumo[];
        if (!this.empresas.length) {
          this.carregandoEmpresas = false;
          return;
        }
        // Skeleton continua ate decidir a empresa (sem flicker).
        this.decidirEmpresa().subscribe((id) => {
          this.empresaId = id;
          this.carregandoEmpresas = false;
          this.carregarMetricas();
        });
      },
      error: (err) => {
        this.carregandoEmpresas = false;
        this.erro = this.feedback.mensagemDeErro(err, 'N\u00e3o foi poss\u00edvel carregar suas empresas.');
      },
    });
  }

  /**
   * Regra: 1) a salva em localStorage, se ainda estiver em /minhas;
   * 2) senao, a com mais boletosEmitidos (campo de contagem em /minhas ou metricas do periodo em paralelo);
   * 3) empate, erro ou tudo zero: a primeira da lista.
   */
  private decidirEmpresa() {
    if (this.empresaId != null && this.empresas.some((x) => x.id === this.empresaId)) return of(this.empresaId);
    const salva = Number(localStorage.getItem(CHAVE_EMPRESA_PAINEL));
    if (salva && this.empresas.some((x) => x.id === salva)) return of(salva);

    const primeira = this.empresas[0].id;
    const contagemLista = (x: any) => x.boletosEmitidos ?? x.totalBoletos ?? x.qtdBoletos;
    if (this.empresas.every((x) => typeof contagemLista(x) === 'number')) {
      return of(this.maior(this.empresas.map((x) => ({ id: x.id, total: contagemLista(x) as number })), primeira));
    }
    if (this.empresas.length === 1) return of(primeira);

    const { de, ate } = this.intervalo();
    return forkJoin(
      this.empresas.map((x) =>
        this.empresaSrv.metricas(x.id, de, ate).pipe(
          map((r: any) => ({ id: x.id, total: Number(r?.data?.boletosEmitidos ?? 0) })),
          catchError(() => of({ id: x.id, total: -1 }))
        )
      )
    ).pipe(
      map((lista) => this.maior(lista, primeira)),
      catchError(() => of(primeira))
    );
  }

  /** Maior total vence; empate com o maior fica com a primeira da lista. */
  private maior(lista: { id: number; total: number }[], primeira: number): number {
    let melhor = { id: primeira, total: 0 };
    for (const item of lista) if (item.total > melhor.total) melhor = item;
    return melhor.id;
  }

  private intervalo() {
    const ate = new Date();
    const de = new Date();
    de.setDate(ate.getDate() - (this.periodo - 1));
    return { de: this.iso(de), ate: this.iso(ate), inicio: de };
  }

  selecionarEmpresa(id: number) {
    if (id == null || id === this.empresaId) return;
    this.empresaId = id;
    localStorage.setItem(CHAVE_EMPRESA_PAINEL, String(id));
    this.carregarMetricas();
  }

  selecionarPeriodo(dias: number) {
    if (this.periodo === dias) return;
    this.periodo = dias;
    this.carregarMetricas();
  }

  carregarMetricas() {
    if (this.empresaId == null) return;
    const { de: deIso, ate: ateIso, inicio: de } = this.intervalo();
    this.carregandoMetricas = true;
    this.erro = null;
    this.empresaSrv.metricas(this.empresaId, deIso, ateIso).subscribe({
      next: (res: any) => {
        this.carregandoMetricas = false;
        this.metricas = res?.data as MetricasEmpresa;
        this.montarBarras(de, this.periodo, this.metricas?.porDia ?? []);
      },
      error: (err) => {
        this.carregandoMetricas = false;
        this.metricas = null;
        this.barras = [];
        this.erro = err?.status === 404
          ? 'Empresa n\u00e3o encontrada ou sem acesso.'
          : this.feedback.mensagemDeErro(err, 'N\u00e3o foi poss\u00edvel carregar as m\u00e9tricas.');
      },
    });
  }

  recarregar() {
    if (this.empresas.length) this.carregarMetricas(); else this.carregarEmpresas();
  }

  formatarCnpj(v: string): string {
    const d = (v || '').replace(/\D/g, '');
    return d.length === 14 ? d.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, '$1.$2.$3/$4-$5') : v;
  }

  /** Preenche todos os dias do periodo (o back pode omitir dias sem movimento). */
  private montarBarras(inicio: Date, dias: number, porDia: MetricaDia[]) {
    const mapa = new Map(porDia.map((p) => [String(p.data).substring(0, 10), p]));
    const passo = dias <= 7 ? 1 : dias <= 30 ? 5 : 15;
    const lista: BarraDia[] = [];
    for (let i = 0; i < dias; i++) {
      const d = new Date(inicio);
      d.setDate(inicio.getDate() + i);
      const chave = this.iso(d);
      const p = mapa.get(chave);
      const autenticas = Number(p?.autenticas ?? 0);
      const suspeitas = Number(p?.suspeitas ?? 0);
      lista.push({
        data: chave, autenticas, suspeitas, total: autenticas + suspeitas, pctAut: 0, pctSus: 0,
        rotulo: `${chave.substring(8, 10)}/${chave.substring(5, 7)}`,
        marcar: i % passo === 0 || i === dias - 1,
      });
    }
    this.maxDia = Math.max(1, ...lista.map((b) => b.total));
    lista.forEach((b) => {
      b.pctAut = (b.autenticas / this.maxDia) * 100;
      b.pctSus = (b.suspeitas / this.maxDia) * 100;
    });
    this.barras = lista;
  }

  private iso(d: Date): string {
    const p = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}`;
  }
}