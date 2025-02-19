import { TestBed } from '@angular/core/testing';

import { PjuridicaService } from './pjuridica.service';

describe('PjuridicaService', () => {
  let service: PjuridicaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PjuridicaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
