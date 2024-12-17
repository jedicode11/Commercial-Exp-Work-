import { SalesRepresentativeEntity } from './salesRepresentative.entity';
import { Column, Entity, Index, JoinColumn, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';

@Entity()
@Index(['country', 'zipCode', 'representative'], { unique: true })
export class SalesPostcodeEntity {
  @PrimaryGeneratedColumn()
  id: number;

  // @Column()
  // standId: string;

  @Column()
  country: string;

  @Column()
  zipCode: number;

  @Column({ nullable: true })
  zipCodeTo: number;

  @ManyToOne(() => SalesRepresentativeEntity, SalesRepresentativeEntity => SalesRepresentativeEntity.salesId, { cascade: true })
  @JoinColumn({ name: 'salesId' })
  representative: SalesRepresentativeEntity;
}
