import { Component, OnInit } from '@angular/core';
import { Pages } from 'src/app/interfaces/pages';

@Component({
  selector: 'app-suporte',
  templateUrl: './suporte.page.html',
  styleUrls: ['./suporte.page.scss'],
})
export class SuportePage implements OnInit {
  ngOnInit(): void {
  }

  public appPages: Array<Pages>;

  constructor(
  ) {
    this.appPages = [
      {
        title: 'Abrir Chamado',
        url: '/suporte-contato',
        direct: 'forward',
        icon: 'call'
      },
      {
        title: 'Chamados',
        url: '/suporte-chamados',
        direct: 'forward',
        icon: 'build'
      },
    ];
  }

}
