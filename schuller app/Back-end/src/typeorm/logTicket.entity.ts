import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';

@Entity()
export class LogTicketEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  timestamp: string;

  @Column()
  ticketCode: string;

  @Column({ nullable: true })
  location: string;

  @Column({ nullable: true })
  adress_suid: string;
}
