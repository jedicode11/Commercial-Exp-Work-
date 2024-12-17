export interface TicketInfo {
  status: 'ok' | 'error';
  ticket: {
    badge_data: {
      country: string;
      email: string;
      company: string;
      last_name: string;
      first_name: string;
    };
    article_id: string;
    name: {
      en: string;
      fr: string;
      de: string;
      it: string;
    };
    barcode: string;
    custom_data: any;
    address_suid: string;
  };
}
