import { animate, query, stagger, style, transition, trigger } from '@angular/animations';
import { Component } from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss'],
  animations: [
    trigger('pageAnimations', [
      transition(':enter', [
        query('.welcome-container, .action-card', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger(120, [
            animate('600ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
          ])
        ])
      ])
    ])
  ]
})
export class HomePage {
  constructor() {}
}
