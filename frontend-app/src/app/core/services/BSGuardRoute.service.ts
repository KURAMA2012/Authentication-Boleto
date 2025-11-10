import { BSAuth } from './BSAuth.service';
import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import {Location} from '@angular/common';

@Injectable()
export class BSGuardRoute implements CanActivate {
    constructor(private bsAuth:BSAuth, private router:Router, private location:Location) {

    }
    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | boolean {
        console.log(this.router)
        console.log(this.bsAuth.usuario === undefined )
        /* Usuario nao autenticado, redireciona para rota de Login */
        if (this.bsAuth.usuario == null ||  this.bsAuth.usuario === undefined ){
            console.log(this.router)
            if (this.router['url'] == '/login' || this.router['url'] == '/' ){
                this.router.navigate(['/login'])
            }else{
                this.location.back()
            }
        }

        if (this.bsAuth.usuario.autenticado !== undefined && this.bsAuth.usuario.autenticado ){
           
            return true
        }
        return false;
    }
}
