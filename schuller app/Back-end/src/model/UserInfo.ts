export enum UserRole {
  PORTALUSER = 'PORTALUSER',
}

export interface UserInfo {
  status: 'ok' | 'error';
  user: {
    roles: UserRole[];
    name: string;
    email: string;
    suid: string;
    modification_date: string;
    creation_date: string;
    groups: string[];
    fullname: string;
    is_released: string;
  };
  address: {
    street: string;
    company3: string;
    company4: string;
    email: string;
    webpage: string;
    id: string;
    second_name: string;
    zip_pobox: string;
    region: string;
    mobile_extension: string;
    name: string;
    suid: string;
    street2: string;
    title: string;
    street3: string;
    division: string;
    position: string;
    pobox: string;
    zip_street: string;
    house_no_extension: string;
    city2: string;
    city: string;
    house_no: string;
    fax: string;
    street4: string;
    company: string;
    gender: string;
    country: string;
    phone: string;
    title2: string;
    company2: string;
    // freefields: {
    //   admsk: string;
    //   stand_number: string;
    //   exhibitor_id: string;
    //   reg_barcode_url: string;
    //   eshop_countries: string;
    //   business_registration_number: string;
    //   ext_company_id: string;
    //   sprache: string;
    //   privacy_provisions: string;
    //   gutschein_code: string;
    //   plain_password: string;
    //   reg_creation_date: string;
    //   member_id: string;
    //   einlader: string;
    //   reg_language: string;
    //   vat_no: string;
    //   ext_person_id: string;
    //   salesforce_id: string;
    //   sonderkennung: string;
    //   kundennrsk: string;
    //   manual_import_exhibitor_id: string;
    //   access_code: string;
    //   passwort: string;
    //   sb_customer_id: string;
    //   primary_contact_name: string;
    //   stand_name: string;
    // };
    freefields: Record<string, string>;
    first_name: string;
    last_name: string;
  };
  registration: {
    survey: [];
  };
}
