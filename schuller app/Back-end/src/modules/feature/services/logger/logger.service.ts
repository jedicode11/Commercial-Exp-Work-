import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { LogSmsEntity } from './../../../../typeorm/logSms.entity';
import { LogTicketEntity } from './../../../../typeorm/logTicket.entity';
import { ScanTicketEntity } from '../../../../typeorm/scanTicket.entity';
import { ScanTicket } from 'src/model/ScanTicket';

@Injectable()
export class LoggerService {
  constructor(
    @InjectRepository(LogTicketEntity) private readonly logTicketRepository: Repository<LogTicketEntity>,
    @InjectRepository(LogSmsEntity) private readonly logSmsRepository: Repository<LogSmsEntity>,
    @InjectRepository(ScanTicketEntity) private readonly scanTicketRepository: Repository<ScanTicketEntity>,
  ) {}

  async logScan(ticket: string, location: string, adress_suid: string) {
    const logTicketEntity = new LogTicketEntity();
    logTicketEntity.timestamp = Date.now().toString();
    logTicketEntity.ticketCode = ticket;
    logTicketEntity.location = location;
    logTicketEntity.adress_suid = adress_suid;
    await this.logTicketRepository.save(logTicketEntity);
  }

  async logSms(sales_id: string, ticket: string, location: string) {
    const logSmsEntity = new LogSmsEntity();
    logSmsEntity.timestamp = Date.now().toString();
    logSmsEntity.salesId = sales_id;
    logSmsEntity.ticketCode = ticket;
    logSmsEntity.location = location;
    await this.logSmsRepository.save(logSmsEntity);
  }

  async createScanTicketEntity(data: ScanTicket, ticket: string) {
    const scanTicket = new ScanTicketEntity();
    scanTicket.decodedString = ticket;
    scanTicket.gender = data.gender;
    scanTicket.firstName = data.first_name;
    scanTicket.lastName = data.last_name;
    scanTicket.fullname = data.fullname;
    scanTicket.company = data.company;
    scanTicket.country = data.country;
    scanTicket.city = data.city;
    scanTicket.zipCode = data.zip_code;
    scanTicket.salesContactId = data.sales_contact_id;
    scanTicket.dateTimeNow = new Date();
    await this.scanTicketRepository.save(scanTicket);
    // const allEntity = this.SalesRepository.find();
  }

  async getScanTicket(): Promise<ScanTicketEntity[]> {
    return this.scanTicketRepository.find({
      take: 20,
      order: {
        dateTimeNow: 'DESC',
      },
    });
  }
}
