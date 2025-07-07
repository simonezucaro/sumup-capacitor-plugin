import { TestBed } from '@angular/core/testing';

import { SumupService } from './sumup.service';

describe('SumupService', () => {
  let service: SumupService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SumupService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
