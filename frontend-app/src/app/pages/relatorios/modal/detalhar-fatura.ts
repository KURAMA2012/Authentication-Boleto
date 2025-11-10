import { OnInit, Component, Input } from "@angular/core";
import { ModalController } from "@ionic/angular";


@Component({
    templateUrl: './detalhar-fatura.html'
})
export class DetalharFaturaComponent implements OnInit {
  @Input() titulo: string

  @Input() fatura:any 


  constructor(
    private modalCtrl:ModalController
  ) { }

  ngOnInit() {
  }

  fecharModal(){
    this.modalCtrl.dismiss();
  }

}
