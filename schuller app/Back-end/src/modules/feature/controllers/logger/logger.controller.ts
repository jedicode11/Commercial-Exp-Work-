import { Controller, HttpCode, Get } from '@nestjs/common';
import { LoggerService } from './../../services/logger/logger.service';
@Controller('logger')
export class LoggerController {
  constructor(private loggerService: LoggerService) {}
  @Get()
  @HttpCode(200)
  async get() {
    return await this.loggerService.getScanTicket();
  }
}
