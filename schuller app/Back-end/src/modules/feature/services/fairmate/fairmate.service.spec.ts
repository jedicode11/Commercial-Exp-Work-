import { Test, TestingModule } from '@nestjs/testing';
import { FairmateService } from './fairmate.service';

describe('FairmateService', () => {
  let service: FairmateService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [FairmateService],
    }).compile();

    service = module.get<FairmateService>(FairmateService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
