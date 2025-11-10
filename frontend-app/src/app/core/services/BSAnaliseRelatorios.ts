import { Injectable } from "@angular/core";
import { RelatorioAnalise } from "src/app/interfaces/RelatorioAnalise";
import { DomSanitizer } from "@angular/platform-browser";

@Injectable()
export class BSAnaliseRelatorios {
    public relatorios: Array<RelatorioAnalise> = [];
    constructor(
        private domSanitizer: DomSanitizer,
    ) {

    }

    addRelatorios(relatorio) {
        this.relatorios.push(relatorio)
    }

    removeRelatorio(relatorio) {
        let index = this.relatorios.indexOf(relatorio)
        this.relatorios.splice(index, 1)
    }

    getPrepareUrl(url){
        return this.domSanitizer.bypassSecurityTrustResourceUrl(url)
    }
}
