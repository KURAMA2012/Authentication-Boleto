import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SuportePage } from './suporte.page';

describe('SuportePage', () => {
  let component: SuportePage;
  let fixture: ComponentFixture<SuportePage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SuportePage ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SuportePage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
