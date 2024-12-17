import { Column, Entity, PrimaryGeneratedColumn, CreateDateColumn } from 'typeorm';

@Entity()
export class ScanTicketEntity {
  //Generate Id
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ nullable: true })
  decodedString: string;

  @Column()
  gender: string;

  @Column()
  firstName: string;

  @Column()
  lastName: string;

  @Column()
  fullname: string;

  @Column()
  company: string;

  @Column()
  country: string;

  @Column()
  city: string;

  @Column()
  zipCode: string;

  @Column()
  salesContactId: string;

  @CreateDateColumn()
  dateTimeNow: Date;
}
