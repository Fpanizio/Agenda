import { TestBed } from '@angular/core/testing';

import { PfisicaService } from './pfisica.service';

describe('PfisicaService', () => {
  let service: PfisicaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PfisicaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
