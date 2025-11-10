import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { empresaAuthGuard } from './empresa-auth.guard';

describe('empresaAuthGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => empresaAuthGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
