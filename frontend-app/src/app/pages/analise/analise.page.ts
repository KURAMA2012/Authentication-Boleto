import { Component, OnInit } from '@angular/core';
import { BSAnaliseRelatorios } from 'src/app/core/services/BSAnaliseRelatorios';
import { RelatorioAnalise } from 'src/app/interfaces/RelatorioAnalise';
import { BSMessage } from 'src/app/core/services/BSMessage.service';

@Component({
  selector: 'app-analise',
  templateUrl: './analise.page.html',
  styleUrls: ['./analise.page.scss'],
})
export class AnalisePage implements OnInit {
  public relatorios: Array<RelatorioAnalise> = [];

  ngOnInit(): void {
  }

  constructor(
    private bsAnaliseRelatorios: BSAnaliseRelatorios,
    private bsMessage: BSMessage

  ) {

    for (let item of this.bsAnaliseRelatorios.relatorios) {
      console.log('Url analise: ' + item.url)
      this.relatorios.push(
        {
          titulo: item.titulo,
          url: this.bsAnaliseRelatorios.getPrepareUrl(item.url)
        }
      )
    }

    if (this.relatorios.length == 0) {
      this.bsMessage.informa("Não existem relatórios para análise")
    }
  }

  removerAnalise(item) {
    let index = this.relatorios.indexOf(item)
    this.relatorios.splice(index, 1)
  }
}
