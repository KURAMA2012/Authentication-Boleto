import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SuporteChatPage } from './suporte-chat.page';

describe('SuporteChatPage', () => {
  let component: SuporteChatPage;
  let fixture: ComponentFixture<SuporteChatPage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SuporteChatPage ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SuporteChatPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
