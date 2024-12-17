import { HttpService } from '@nestjs/axios';
import { Injectable } from '@nestjs/common';
import { map, tap } from 'rxjs';
import { TicketInfo } from './../../../../model/TicketInfo';
import { UserInfo } from './../../../../model/UserInfo';

const baseUrl = 'https://shop3-fairmateapi.fairmate.de';

@Injectable()
export class FairmateService {
  private userUrl = (fair_id: string, ticket_code: string) => baseUrl + `/fairs/${fair_id}/tickets/${ticket_code}`;
  private userInfoUrl = (user_id: string) => baseUrl + `/customers/by-customer-id/${user_id}`;
  private entryActionUrl = (fair_id: string, ticket_code: string) => baseUrl + `/fairs/${fair_id}/tickets/${ticket_code}/entry?force=true`;

  constructor(private readonly httpService: HttpService) {}

  getUser(ticket: string, fair_id: string) {
    return this.httpService
      .get<TicketInfo>(this.userUrl(fair_id, ticket), {
        headers: {
          'X-Api-Key': '6e8e819a81aa4a828ada08e9afd2517a',
        },
      })
      .pipe(
        map(res => res.data),
        tap(res => console.log(res)),
      );
  }

  getUserInfo(user_id: string) {
    return this.httpService
      .get<UserInfo>(this.userInfoUrl(user_id), {
        headers: {
          'X-Api-Key': '6e8e819a81aa4a828ada08e9afd2517a',
        },
      })
      .pipe(map(res => res.data));
  }

  entryAction(ticket: string, fair_id: string) {
    return this.httpService
      .post<boolean>(
        this.entryActionUrl(fair_id, ticket),
        {},
        {
          headers: {
            'X-Api-Key': '6e8e819a81aa4a828ada08e9afd2517a',
          },
        },
      )
      .pipe(map(res => res.data));
  }
}
