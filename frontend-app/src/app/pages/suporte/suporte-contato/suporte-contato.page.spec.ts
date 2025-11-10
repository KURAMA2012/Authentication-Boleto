import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SuporteContatoPage } from './suporte-contato.page';

describe('SuporteContatoPage', () => {
  let component: SuporteContatoPage;
  let fixture: ComponentFixture<SuporteContatoPage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SuporteContatoPage ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SuporteContatoPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
