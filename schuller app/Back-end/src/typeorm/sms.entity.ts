import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';

@Entity()
export class SmsEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  salesId: string;

  @Column()
  recipient: string;

  @Column()
  message: string;

  @Column()
  status: string;
}
