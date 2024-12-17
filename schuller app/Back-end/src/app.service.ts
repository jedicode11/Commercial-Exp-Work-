import { Injectable } from '@nestjs/common';
import { DynamicConfigService } from 'nestjs-dynamic-config';
import { Config } from './model/Config.model';

@Injectable()
export class AppService {
  config: Config;
  constructor(private readonly configService: DynamicConfigService<Config>) {
    configService.configStream().subscribe(config => {
      this.config = config;
    });
  }

  getDefault(): string {
    return 'OSIT';
  }

  validatePin(pin: string | number) {
    return !!this.config.locations.find(l => l.pin === pin);
  }

  getLocations() {
    return this.config.locations.map(l => ({ code: l.code, name: l.name }));
  }

  getLocationByPin(pin: string | number) {
    return this.config.locations.find(l => l.pin === pin);
  }

  validateLocation(code: string) {
    return !!this.config.locations.find(l => l.code === code);
  }
}
