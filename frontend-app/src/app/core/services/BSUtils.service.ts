import { Injectable } from '@angular/core';


@Injectable()
export class BSUtils {
    public  loadOptions(list){
        var options:any = [{label: 'Seleção', value: null }];
        for (let item of list){
            if (item.id === undefined){
                options.push({ label: item.nome, value: {codigo: item.codigo , nome:item.nome }});
            }else{
                options.push({ label: item.nome, value: {id: item.id, nome:item.nome }});
            }
        }

        return options
    }

    public  convertOptions(list){
        var options = [];
        for (let item of list){
            options.push({id: item.id, nome:item.nome });
        }

        return options
    }

    public removeCaractererEspecial(value){
        try{
            return value.replace(/[-[\]{}()*+?%&amp;amp;amp;amp;amp;@!?¨_:;.'"<>/=\\^$|#\b]/g, "")
        }catch(e){
            return value
        }
    }

    public clone(object){
        return Object.assign([], object)
    }

    public  getPeriodo(date) : string{
        var dataBase = new Date(date)
        const meses = ["Janeiro", "Fevereiro", "Março", "Abril", "Mario", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"];

        return meses[dataBase.getMonth()]
    }
}