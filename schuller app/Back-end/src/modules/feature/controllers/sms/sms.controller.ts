import { Body, Controller, HttpCode, HttpException, Post } from '@nestjs/common';
import LocalizedStrings from 'localized-strings';
import { DynamicConfigService } from 'nestjs-dynamic-config';
import { catchError, from, lastValueFrom, tap } from 'rxjs';
import { Config } from 'src/model/Config.model';
import { SmsService } from '../../services/sms/sms.service';
import { SalesRepresentativeEntity } from './../../../../typeorm/salesRepresentative.entity';
import { LoggerService } from './../../services/logger/logger.service';
import { SalesService } from './../../services/sales/sales.service';
import { SendDto } from './dto/Send.dto';

const Salutation: Record<string, string> = {
  '1': 'Herr',
  '2': 'Frau',
};

// const defaultSmsTemplate = '{prefix} / {last_name} / {salut} / {company_name} / {company_city}';
const defaultSmsTemplate = '{prefix} / {salut} / {first_name} / {last_name} / {company_name} / {company_city}';

@Controller('sms')
export class SmsController {
  config: Config;
  localizedStrings: any;

  constructor(
    private readonly smsService: SmsService,
    private readonly salesService: SalesService,
    private readonly configService: DynamicConfigService<Config>,
    private readonly loggerService: LoggerService,
  ) {
    configService.configStream().subscribe(config => {
      this.config = config;
    });
    this.localizedStrings = new LocalizedStrings({
      en: {
        default: 'How do you want your egg today?',
      },
      it: {
        default: 'Come vuoi il tuo uovo oggi?',
      },
    });
  }

  @Post('send')
  @HttpCode(200)
  async get(@Body() body: SendDto) {
    console.log('🚀 ~ file: sms.controller.ts ~ line 47 ~ SmsController ~ get ~ body', body);
    let salesRepresentative: SalesRepresentativeEntity;
    // get sales from sales_id
    if (body.sales_id) {
      salesRepresentative = await this.salesService.getRepresentativeById(body.sales_id);
      console.log('🚀 ~ file: sms.controller.ts ~ line 52 ~ SmsController ~ sms/send ~ salesRepresentative{1)', salesRepresentative);
    }

    // if not found, get sales from zip_code
    if (!salesRepresentative) {
      const salesCountry = (body.country && body.country.trim()) || '';
      const salesZipCode = (body.zip_code && body.zip_code.trim().replace(/^0+/, '')) || '';
      console.log('🚀 ~ file: sms.controller.ts ~ line 59 ~ SmsController ~ sms/send ~ salesCountry:', salesCountry, ' salesZipCode:', salesZipCode);

      salesRepresentative = await this.salesService.getRepresentativeByZipCode(salesCountry, salesZipCode);
      // salesRepresentative = await this.salesService.getRepresentativeByZipCode(body.country.trim(), body.zip_code.trim().replace(/^0+/, ''));

      console.log('🚀 ~ file: sms.controller.ts ~ line 64 ~ SmsController ~ sms/send ~ salesRepresentative{2)', salesRepresentative);

      // if still not found, exit
      if (!salesRepresentative) {
        throw new HttpException('Sales Not Found', 404);
      }
    }
    console.log('🚀 ~ file: sms.controller.ts ~ line 71 ~ SmsController ~ sms/send ~ salesRepresentative', salesRepresentative);

    //    const sales_phone = salesRepresentative.salesPhone.replace(/\D/g, '');
    const sales_phone = salesRepresentative.salesPhone.replace(/\s/g, '');
    //    const sales_phone = '+359888888633';
    console.log('🚀 ~ file: sms.controller.ts ~ line 76 ~ SmsController ~ sms/send ~ sales_phone', sales_phone);
    //    return;

    // const last_name = body.last_name || body.lastName;
    // const salutation = Salutation[body.gender];
    // const city = body.city || body.company_ort;
    // const sms = [body.location_code, salutation, last_name, body.company, city]
    //   .filter(Boolean)
    //   .map(s => s.trim())
    //   .filter(Boolean)
    //   .join(' / ');
    const smsTemplate: string = salesRepresentative.smsTemplate || defaultSmsTemplate;
    const smsMessage = this.localizedStrings.formatString(smsTemplate, {
      prefix: body.location_code,
      salut: Salutation[body.gender],
      first_name: body.first_name || body.firstName,
      last_name: body.last_name || body.lastName,
      company_name: body.company,
      company_city: body.city || body.company_ort,
    });
    console.log('🚀 ~ file: sms.controller.ts ~ line 96 ~ SmsController ~ sms', smsMessage);
    //    return;

    this.loggerService.logSms(salesRepresentative.salesId, body.ticket, body.location_code).then();

    // test congig: if true => send SMS, if false => prevent sending
    if (!this.config.do_send_sms) {
      return;
    }

    return await lastValueFrom(
      from(this.smsService.sendSms(sales_phone, smsMessage)).pipe(
        tap(res => {
          console.log('🚀 ~ file: sms.controller.ts ~ line 109 ~ SmsController ~ res', res);
        }),
        catchError(err => {
          // console.log('🚀 ~ file: sms.controller.ts ~ line 112 ~ SmsController ~ err', err);
          throw new HttpException(err.response ?? 'Generic Server Error', err.status ?? 500);
        }),
      ),
    );
  }
}
