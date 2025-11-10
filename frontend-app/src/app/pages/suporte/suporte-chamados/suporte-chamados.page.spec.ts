import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SuporteChamadosPage } from './suporte-chamados.page';

describe('SuporteChamadosPage', () => {
  let component: SuporteChamadosPage;
  let fixture: ComponentFixture<SuporteChamadosPage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SuporteChamadosPage ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SuporteChamadosPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
