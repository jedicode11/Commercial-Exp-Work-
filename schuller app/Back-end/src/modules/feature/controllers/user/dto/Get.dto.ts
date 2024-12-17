import { IsOptional, IsString } from 'class-validator';

export class GetDto {
  @IsString()
  readonly ticket: string;

  @IsString()
  @IsOptional()
  readonly fair_id?: string;

  @IsString()
  @IsOptional()
  readonly location?: string;
}
