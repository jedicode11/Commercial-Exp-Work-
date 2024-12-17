import { PinLocation } from './PinLocation.model';

export interface Config {
  fallback_fair_id?: string;
  locations?: PinLocation[];
  sales_contact_ids_ff_key?: string;
  textmagic?: {
    username: string;
    apiKey: string;
  };
  sipgateio?: {
    username: string;
    password: string;
    tokenId: string;
    token: string;
  };
  do_send_sms: boolean;
}
