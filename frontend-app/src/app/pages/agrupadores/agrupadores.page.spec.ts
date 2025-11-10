import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AgrupadorPage } from './agrupadores.page';

describe('AgrupadorPage', () => {
  let component: AgrupadorPage;
  let fixture: ComponentFixture<AgrupadorPage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AgrupadorPage ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AgrupadorPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
