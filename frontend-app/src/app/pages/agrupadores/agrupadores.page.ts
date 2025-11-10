import { Component, OnInit } from '@angular/core';
import { Pages } from 'src/app/interfaces/pages';
import { RAJResource } from 'src/app/core/services/raj-model.services';
import { AppComponent } from 'src/app/app.component';

@Component({
  selector: 'app-agrupadores',
  templateUrl: './agrupadores.page.html',
  styleUrls: ['./agrupadores.page.scss'],
})
export class AgrupadorPage implements OnInit {

  ngOnInit(): void {
  }

  public appPages: Array<Pages> = [];
  urlRelatorio:any[]

  constructor(
    private rajResource:RAJResource,
    private appComponente: AppComponent
  ) {
    

    this.appPages = []
    this.urlRelatorio
    // this.rajResource.getGrupoRelatorios()
    //   .subscribe(
    //     data => {
    //       let response  = data['result'][0]['Botoes']
    //       for (let item of response){
    //         this.appPages.push(
    //           {
    //             title: item['Caption'],
    //             url: '/relatorios/' + `{"agrupador":"${String(item['Caption'])}","caption":" ${item['Caption']}"}`,
    //             direct: 'forward',
    //             icon: 'albums-outline'
    //           }
    //         )
    //       }
    //       debugger
    //       //this.appComponente.appPagesRecursos = this.appPages
    //     }
    //   );
  }
}
