import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FiltrosPage } from './filtros';


describe('FiltrosPage', () => {
  let component: FiltrosPage;
  let fixture: ComponentFixture<FiltrosPage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FiltrosPage ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FiltrosPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
