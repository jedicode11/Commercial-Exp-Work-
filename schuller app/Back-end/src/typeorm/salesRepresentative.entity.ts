import { AllowNull, HasMany } from 'sequelize-typescript';
import { Column, Entity, OneToMany, PrimaryColumn } from 'typeorm';
import { SalesPostcodeEntity } from './salesPostcode.entity';

@Entity()
export class SalesRepresentativeEntity {
  // @PrimaryGeneratedColumn()
  // id: number;

  // @Column()
  // standId: string;

  @Column()
  @PrimaryColumn()
  salesId: string;

  @Column()
  salesFirstName: string;

  @Column()
  salesLastName: string;

  @Column()
  salesName: string;

  // @Column()
  // salesLang: string;

  @Column()
  salesPhone: string;

  // @Column()
  // salesPhoneType: string;

  // @Column()
  // postalCodeArea: string;

  @Column()
  salesEmail: string;

  @OneToMany(() => SalesPostcodeEntity, postcode => postcode.representative)
  postcodes: SalesPostcodeEntity[];

  @Column({ nullable: true })
  smsTemplate: string;
}
