import { Body, Controller, HttpCode, HttpException, Post, UseInterceptors } from '@nestjs/common';
import { DynamicConfigService } from 'nestjs-dynamic-config';
import { catchError, lastValueFrom, map, switchMap, tap } from 'rxjs';
import { FairmateService } from '../../services/fairmate/fairmate.service';
import { SalesService } from '../../services/sales/sales.service';
import { NotFoundInterceptor } from './../../../../interceptors/not-found/not-found.interceptor';
import { Config } from './../../../../model/Config.model';
import { LoggerService } from './../../services/logger/logger.service';
import { GetDto } from './dto/Get.dto';
import { GetUserDto } from './dto/GetUser.dto';
import { GetUserInfoDto } from './dto/GetUserInfo.dto';

@Controller('user')
@UseInterceptors(NotFoundInterceptor)
export class UserController {
  config: Config;

  constructor(
    private readonly loggerService: LoggerService,
    private readonly fairmateService: FairmateService,
    private readonly configService: DynamicConfigService<Config>,
    private readonly salesService: SalesService,
  ) {
    configService.configStream().subscribe(config => {
      this.config = config;
    });
  }
  @Post()
  @HttpCode(200)
  async get(@Body() body: GetDto) {
    const fair_id = body.fair_id ?? this.config.fallback_fair_id;
    const sales_contact_ids_ff_key = this.config.sales_contact_ids_ff_key;
    const decodedTicket = body.ticket;
    return await lastValueFrom(
      this.fairmateService.getUser(body.ticket, fair_id).pipe(
        tap(ticket_info => {
          if (ticket_info) {
            console.log('======= user(ticket):', body.ticket);
            this.loggerService.logScan(body.ticket, body.location, ticket_info.ticket.address_suid).then();
            this.fairmateService.entryAction(body.ticket, fair_id).subscribe();
          }
        }),
        switchMap(ticket_info =>
          this.fairmateService.getUserInfo(ticket_info.ticket.address_suid).pipe(
            map(async user_info => ({
              gender: user_info.address.gender ?? '',
              first_name: (user_info.address.first_name || ticket_info.ticket.badge_data.first_name) ?? '',
              last_name: (user_info.address.name || ticket_info.ticket.badge_data.last_name) ?? '',
              fullname: user_info.user.fullname ?? '',
              company: user_info.address.company ?? '',
              companies: [user_info.address.company, user_info.address.company2, user_info.address.company3, user_info.address.company4].filter(Boolean),
              country: user_info.address.country ?? '',
              city: user_info.address.city ?? '',
              cities: [user_info.address.city, user_info.address.city2].filter(Boolean),
              zip_code: user_info.address.zip_street ?? '',
              zip_codes: [user_info.address.zip_street, user_info.address.zip_pobox].filter(Boolean),
              // sales_contact_id: user_info.address.freefields[sales_contact_ids_ff_key] ?? '',
              sales_contact_ids: [
                sales_contact_ids_ff_key ? user_info.address.freefields[sales_contact_ids_ff_key] : '',
                user_info.address.freefields['salesforce_id'],
              ].filter(Boolean),
              sales_contact_id: await this.getSalesContact(
                user_info.address.freefields[sales_contact_ids_ff_key],
                user_info.address.country,
                user_info.address.zip_street,
              ),
            })),
          ),
        ),
        tap(scanticketforce => {
          scanticketforce.then(scanticketforce => this.loggerService.createScanTicketEntity(scanticketforce, decodedTicket));
        }),
        catchError(err => {
          const msg = (err.response && ((err.response.data && err.response.data) || 'Generic Server Error')) || JSON.stringify(err);
          throw new HttpException(msg, err.status ?? 500);
          //throw new HttpException(err.response && err.response.data ?? 'Generic Server Error' || JSON.stringify(err), err.status ?? 500);
        }),
      ),
    );
  }

  async getSalesContact(salesId: string, country: string, zipCode: string): Promise<string> {
    let salesRepresentative = await this.salesService.getRepresentativeById(salesId);
    console.log('🚀 ~ file: sms.controller.ts ~ line 82 ~ SmsController ~ sms/send ~ salesRepresentative{1)', salesRepresentative);
    if (salesRepresentative) {
      console.log('🚀 ~ file: user.controller.ts ~ line 84 ~ UserController ~ hasSalesContact ~ true');
      return salesRepresentative.salesId;
    }

    // if not found, get sales from zip_code
    else {
      const salesCountry = (country && country.trim()) || '';
      const salesZipCode = (zipCode && zipCode.trim().replace(/^0+/, '')) || '';
      console.log('🚀 ~ file: sms.controller.ts ~ line 92 ~ SmsController ~ sms/send ~ salesCountry:', salesCountry, ' salesZipCode:', salesZipCode);

      salesRepresentative = await this.salesService.getRepresentativeByZipCode(salesCountry, salesZipCode);
      // salesRepresentative = await this.salesService.getRepresentativeByZipCode(body.country.trim(), body.zip_code.trim().replace(/^0+/, ''));

      console.log('🚀 ~ file: sms.controller.ts ~ line 97 ~ SmsController ~ sms/send ~ salesRepresentative{2)', salesRepresentative);

      // if still not found, exit
      if (salesRepresentative) {
        console.log('🚀 ~ file: user.controller.ts ~ line 101 ~ UserController ~ hasSalesContact ~ true');
        return salesRepresentative.salesId;
      }
    }
    console.log('🚀 ~ file: user.controller.ts ~ line 105 ~ UserController ~ hasSalesContact ~ false');
    return '';
  }

  @Post('id')
  @HttpCode(200)
  async getUser(@Body() body: GetUserDto) {
    return await lastValueFrom(
      this.fairmateService.getUser(body.ticket, body.fair_id).pipe(
        tap(ticket_info => {
          if (ticket_info) {
            // this.fairmateService.entryAction(ticket, fair_id).subscribe();
          }
        }),
        map(ticket_info => ticket_info.ticket.address_suid),
        catchError(err => {
          throw new HttpException(err.response.data ?? 'Generic Server Error', err.status ?? 500);
        }),
      ),
    );
  }

  @Post('info')
  @HttpCode(200)
  async getUserInfo(@Body() body: GetUserInfoDto) {
    return await lastValueFrom(
      this.fairmateService.getUserInfo(body.user_id).pipe(
        catchError(err => {
          throw new HttpException(err.response.data ?? 'Generic Server Error', err.status ?? 500);
        }),
      ),
    );
  }
}
