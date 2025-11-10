import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SuporteDownloadsPage } from './suporte-downloads.page';

describe('SuporteDownloadsPage', () => {
  let component: SuporteDownloadsPage;
  let fixture: ComponentFixture<SuporteDownloadsPage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SuporteDownloadsPage ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SuporteDownloadsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
