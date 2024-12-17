import { Body, Controller, HttpCode, HttpException, Post } from '@nestjs/common';
import { ApiOperation, ApiResponse } from '@nestjs/swagger';
import { IsString } from 'class-validator';
import { AppService } from './app.service';

class PinDto {
  @IsString()
  readonly pin: string;
}

@Controller()
export class AppController {
  getHello(): any {
    throw new Error('Method not implemented.');
  }
  constructor(private readonly appService: AppService) {}

  @Post('pin')
  @ApiOperation({ summary: 'Location pin' })
  @ApiResponse({ status: 403, description: 'Forbidden.' })
  @HttpCode(200)
  validatePin(@Body() body: PinDto) {
    const valid = this.appService.validatePin(body.pin);
    if (valid) {
      const res = this.appService.getLocationByPin(body.pin);
      console.log('🚀 ~ file: app.controller.ts ~ line 28 ~ validatePin ~ pin:', body.pin);
      return res;
    } else {
      throw new HttpException('Not authorized', 401);
    }
  }
}
