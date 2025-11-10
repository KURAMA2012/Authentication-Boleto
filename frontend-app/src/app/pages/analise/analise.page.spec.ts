import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AnalisePage } from './analise.page';

describe('AgrupadorPage', () => {
  let component: AnalisePage;
  let fixture: ComponentFixture<AnalisePage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AnalisePage ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AnalisePage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
