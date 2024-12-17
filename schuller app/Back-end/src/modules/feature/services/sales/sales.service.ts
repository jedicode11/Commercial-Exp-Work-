import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { CsvParser } from 'nest-csv-parser';
import { Brackets, Repository } from 'typeorm';
import { SalesPostcode } from './../../../../model/SalesPostcode.model';
import { SalesRepresentative } from './../../../../model/SalesRepresentative.model';
import { SalesPostcodeEntity } from './../../../../typeorm/salesPostcode.entity';
import { SalesRepresentativeEntity } from './../../../../typeorm/salesRepresentative.entity';

@Injectable()
export class SalesService {
  constructor(
    private readonly csvParser: CsvParser,
    @InjectRepository(SalesPostcodeEntity) private readonly salesPostcodeRepository: Repository<SalesPostcodeEntity>,
    @InjectRepository(SalesRepresentativeEntity) private readonly salesRepresentativeRepository: Repository<SalesRepresentativeEntity>,
  ) {}

  async parseSalesRepresentatives(stream) {
    const config = {
      strict: true,
      separator: ';',
      headers: ['key', 'name', 'phone', 'email', 'template'],
      skipLines: 1,
    };
    const sales = await this.csvParser.parse(stream, SalesRepresentative, null, null, config);

    const salesRepresentativeEntities = sales.list.map((saleRepresentative: SalesRepresentative) => {
      const salesRepresentativeEntity = new SalesRepresentativeEntity();
      const [salesFirstName, salesLastName] = (saleRepresentative.name ?? '').trim().replace(/\s+/g, ' ').split(' ');
      salesRepresentativeEntity.salesFirstName = salesFirstName ?? '';
      salesRepresentativeEntity.salesLastName = salesLastName ?? '';
      salesRepresentativeEntity.salesName = (saleRepresentative.name ?? '').trim();
      salesRepresentativeEntity.salesId = (saleRepresentative.key ?? '').trim();
      salesRepresentativeEntity.salesPhone = (saleRepresentative.phone ?? '').trim();
      salesRepresentativeEntity.salesEmail = (saleRepresentative.email ?? '').trim();
      salesRepresentativeEntity.smsTemplate = (saleRepresentative.template ?? '').trim();

      return salesRepresentativeEntity;
    });

    const result = await this.salesRepresentativeRepository.save(salesRepresentativeEntities);

    return result;
  }

  async clearRepresentatives() {
    await this.salesRepresentativeRepository.delete({});

    return true;
  }

  async getRepresentativeById(salesId: string) {
    console.log('🚀 ~ file: sales.service.ts ~ line 53 ~ SalesService ~ getRepresentativeById ~ salesId', salesId);
    return await this.salesRepresentativeRepository.findOne({ where: { salesId: salesId } });
  }

  async getRepresentativeByZipCode(country: string, _zipCode: string) {
    if (!country || !_zipCode) {
      return;
    }
    const zipCode = _zipCode.replace(/\D/g, '');
    const qb = this.salesRepresentativeRepository
      .createQueryBuilder('r')
      .innerJoin('sales_postcode_entity', 'p', 'r.salesId = p.salesId AND p.country = :country', { country })
      .select(['r.*'])
      .where(
        new Brackets(qb1 => {
          qb1.where('p.zipCodeTo = 0').andWhere('p.zipCode = :zipCode', { zipCode });
        }),
      )
      .orWhere(
        new Brackets(qb1 => {
          qb1.where('p.zipCodeTo > 0').andWhere('p.zipCode <= :zipCode', { zipCode }).andWhere('p.zipCodeTo >= :zipCode', { zipCode });
        }),
      )
      .orWhere(
        new Brackets(qb1 => {
          qb1.where('p.zipCodeTo is null').andWhere('p.zipCode <= :zipCode', { zipCode });
        }),
      )
      .orWhere('p.zipCode = 0');

    //console.log(qb.getQueryAndParameters());
    // console.log(qb.getSql());

    return await qb.getRawOne();
  }

  async parseSalesPostcodes(stream) {
    const config = {
      strict: true,
      separator: ';',
      headers: ['country', 'zip', 'zip_to', 'key'],
      skipLines: 1,
    };
    const sales = await this.csvParser.parse(stream, SalesPostcode, null, null, config);

    const salesPostcodeEntities = await Promise.all(
      sales.list.map(async (salePostcode: SalesPostcode) => {
        const salesPostcodeEntity = new SalesPostcodeEntity();
        salesPostcodeEntity.country = (salePostcode.country ?? '').trim();
        salesPostcodeEntity.zipCode = +(salePostcode.zip ?? '').trim().replace(/^0+/, '');
        salesPostcodeEntity.zipCodeTo = salePostcode.zip_to === '' ? null : +(salePostcode.zip_to ?? '').trim().replace(/^0+/, '');
        // salesPostcodeEntity.salesId = (salePostcode.key ?? '').trim();
        const salesId = (salePostcode.key ?? '').trim();
        salesPostcodeEntity.representative = await this.salesRepresentativeRepository.findOne({ where: { salesId: salesId } });

        return salesPostcodeEntity;
      }),
    );

    const result = await this.salesPostcodeRepository.save(salesPostcodeEntities);

    return result;
  }

  async clearSalesPostcodes() {
    await this.salesPostcodeRepository.delete({});

    return true;
  }
}
