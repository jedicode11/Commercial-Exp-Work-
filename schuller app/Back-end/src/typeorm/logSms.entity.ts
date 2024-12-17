import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';

@Entity()
export class LogSmsEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  timestamp: string;

  @Column()
  salesId: string;

  @Column({ nullable: true })
  ticketCode: string;
  
  @Column({ nullable: true })
  location: string;
}
