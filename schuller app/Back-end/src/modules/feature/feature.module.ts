import { HttpModule } from '@nestjs/axios';
import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { CsvModule } from 'nest-csv-parser';
import { LogSmsEntity } from './../../typeorm/logSms.entity';
import { ScanTicketEntity } from '../../typeorm/scanTicket.entity';
import { LogTicketEntity } from './../../typeorm/logTicket.entity';
import { SalesPostcodeEntity } from './../../typeorm/salesPostcode.entity';
import { SalesRepresentativeEntity } from './../../typeorm/salesRepresentative.entity';
import { SmsEntity } from './../../typeorm/sms.entity';
import { SalesController } from './controllers/sales/sales.controller';
import { SmsController } from './controllers/sms/sms.controller';
import { UserController } from './controllers/user/user.controller';
import { FairmateService } from './services/fairmate/fairmate.service';
import { LoggerService } from './services/logger/logger.service';
import { SalesService } from './services/sales/sales.service';
import { SmsService } from './services/sms/sms.service';

import { LoggerController } from './controllers/logger/logger.controller';
@Module({
  imports: [
    CsvModule,
    HttpModule,
    TypeOrmModule.forFeature([LogTicketEntity, LogSmsEntity, SalesPostcodeEntity, SalesRepresentativeEntity, SmsEntity, ScanTicketEntity]),
  ],
  controllers: [SalesController, SmsController, UserController, LoggerController],
  providers: [FairmateService, LoggerService, SalesService, SmsService],
})
export class FeatureModule {}
