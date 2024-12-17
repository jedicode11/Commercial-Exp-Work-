import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { DynamicConfigService } from 'nestjs-dynamic-config';
import { throwError } from 'rxjs';
import { createSMSModule, sipgateIO } from 'sipgateio';
import * as TMClient from 'textmagic-rest-client';
import { Repository } from 'typeorm';
import { Config } from '../../../../model/Config.model';
import { SmsEntity } from '../../../../typeorm/sms.entity';

@Injectable()
export class SmsService {
  config: Config;

  constructor(
    private readonly configService: DynamicConfigService<Config>,
    @InjectRepository(SmsEntity) private readonly smsRepository: Repository<SmsEntity>,
  ) {
    configService.configStream().subscribe(config => {
      this.config = config;
    });
  }

  sendSms(sales_phone: string, text: string) {
    // return this.httpService.post<number>(this.sendUrl);
    if (process.env.SMS_PROVIDER === 'TextMagic') {
      return this.sendSmsViaTextMagic(sales_phone, text);
    }
    if (process.env.SMS_PROVIDER === 'sipgateIO') {
      return this.sendSmsViaSipgateIo(sales_phone, text);
    }
    return throwError(() => ({
      status: 500,
      response: 'Please specify SMS_PROVIDER',
    }));
  }

  sendSmsViaTextMagic(sales_phone: string, text: string) {
    return new Promise<any>((resolve, reject) => {
      if (!this.config.textmagic) {
        reject({
          status: 500,
          response: 'Missing SMS config',
        });
      } else {
        const { username, apiKey } = this.config.textmagic;
        const c = new TMClient(username, apiKey);

        c.Messages.send({ text, phones: sales_phone }, function (err, res) {
          // console.log('Messages.send() res', res);
          // console.log('Messages.send() err', err);
          if (err) {
            reject({
              status: 500,
              response: res,
            });
          } else {
            resolve(res);
          }
        });
      }
    });
  }

  sendSmsViaSipgateIo(to: string, message: string) {
    return new Promise<any>(async (resolve, reject) => {
      if (!this.config.sipgateio) {
        reject({
          status: 500,
          response: 'Missing SMS config',
        });
      } else {
        // const { username, password } = this.config.sipgateio;
        const { tokenId, token } = this.config.sipgateio;
        let c;
        try {
          c = sipgateIO({ tokenId, token });
        } catch (e) {
          reject({
            status: 500,
            response: e.message,
          });
        }
        if (c) {
          const smsProvider = createSMSModule(c);

          const smsEntity = new SmsEntity();
          smsEntity.message = message;
          smsEntity.recipient = to;
          smsEntity.salesId = '0';
          smsEntity.status = 'sending';
          const sms = await this.smsRepository.save(smsEntity);

          smsProvider
            .send({ message, to, smsId: `s1` })
            .then(res => {
              console.log('sms.send() res', res);
              resolve(res);
            })
            .catch(err => {
              console.log('sms.send() err', err);
              reject({
                status: 500,
                response: err,
              });
            });
        }
      }
    });
  }
}
